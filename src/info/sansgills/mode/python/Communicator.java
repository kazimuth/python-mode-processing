package info.sansgills.mode.python;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import processing.app.exec.StreamRedirectThread;
import processing.core.PApplet;

/**
 * 
 * The glue between the PDE process and the sketch process
 * Handles sending messages to and recieving messages from
 *
 */

public class Communicator {
	private PythonRunner runner;
	private Process sketchProcess;
	
	private StreamRedirectThread outThread;
	private MessageReceiverThread errThread;
	
	private PrintWriter toSketch;
	
	public Communicator (Process sketchProcess, PythonRunner runner){
		this.sketchProcess = sketchProcess;
		this.runner = runner;
		
		outThread = new StreamRedirectThread("JVM Stdout Reader", sketchProcess.getInputStream(), System.out);
		errThread = new MessageReceiverThread(sketchProcess.getErrorStream(), runner);
		
		toSketch = new PrintWriter(sketchProcess.getOutputStream());
		
		outThread.start();
		errThread.start();
	}
	
	public void destroy(){
		errThread.running = false;
		errThread = null;
		outThread = null;
		toSketch.close();
		toSketch = null;
	}
	
	
	// communication methods
	
	/*
	 * Tell sketch to close
	 */
	public void sendClose(){
		toSketch.println("__STOP__"); //hard-coded, what the hell
		System.out.println("__STOP__");
		toSketch.flush();
	}
	
	/*
	 * Send a new sketch
	 */
	public void sendSketch(String[] args){
		StringBuilder out = new StringBuilder("__SKETCH__");
		for(String a : args){
			out.append(" "+a);
		}
		
		
		toSketch.println(out.toString());
		System.out.println(out.toString());
		toSketch.flush();
	}
	
	//private class to handle doing things when the sketch process sends us a message via system.err
	private class MessageReceiverThread extends Thread{
		PythonRunner runner;
		BufferedReader messageReader;
		
		public boolean running;
		
		public MessageReceiverThread(InputStream messageStream, PythonRunner runner){
			this.messageReader = new BufferedReader(new InputStreamReader(messageStream));
			this.runner = runner;
			this.running = true;
		}
		
		public void run() {
			try {
				String currentLine;

				// continually read messages
				while ((currentLine = messageReader.readLine()) != null && running) {
					if (currentLine.indexOf("__STOPPED__") != -1) {
						// sketch telling us it stopped
						runner.parallelStopped();
						return;
					}else if(currentLine.indexOf("__STARTED__") != -1){
						runner.parallelStarted();
					} else{
						System.err.println(currentLine);
					}
				}
			} catch (Exception e) {}
		}
	}
}
