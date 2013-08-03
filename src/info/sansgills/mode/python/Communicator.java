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
	private PythonEditor editor;
	private Process sketchProcess;
	
	private StreamRedirectThread outThread;
	private MessageReceiverThread errThread;
	
	private PrintWriter toSketch;
	
	public Communicator (Process sketchProcess, PythonEditor editor){
		this.sketchProcess = sketchProcess;
		this.editor = editor;
		
		outThread = new StreamRedirectThread("JVM Stdout Reader", sketchProcess.getInputStream(), System.out);
		errThread = new MessageReceiverThread(sketchProcess.getErrorStream(), editor);
		
		toSketch = new PrintWriter(sketchProcess.getOutputStream());
		
		outThread.start();
		errThread.start();
	}
	
	public void close(){
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
		
	}
	
	//private class to handle doing things when the sketch process sends us a message via system.err
	private class MessageReceiverThread extends Thread{
		PythonEditor editor;
		BufferedReader messageReader;
		
		public boolean running;
		
		public MessageReceiverThread(InputStream messageStream, PythonEditor editor){
			this.messageReader = new BufferedReader(new InputStreamReader(messageStream));
			this.editor = editor;
			this.running = true;
		}
		
		public void run() {
			try {
				String currentLine;

				// continually read messages
				while ((currentLine = messageReader.readLine()) != null && running) {
					if (currentLine.indexOf(PApplet.EXTERNAL_STOP) == 0) {
						// sketch telling us it stopped
						editor.internalCloseRunner();
						return;
					}else if (currentLine.indexOf(PApplet.EXTERNAL_MOVE) == 0) {
						//sketch telling us it moved
						String nums = currentLine.substring(currentLine.indexOf(' ') + 1).trim();
						int space = nums.indexOf(' ');
						int left = Integer.parseInt(nums.substring(0, space));
						int top = Integer.parseInt(nums.substring(space + 1));
						editor.setSketchLocation(new Point(left, top));
						return;
					}else{
						System.err.println(currentLine);
					}
				}
			} catch (Exception e) {}
		}
	}
}
