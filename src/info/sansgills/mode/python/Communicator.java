package info.sansgills.mode.python;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.Timer;
import java.util.TimerTask;

import processing.app.exec.StreamRedirectThread;

/**
 * 
 * A class that sends data to and gets data from the running sketch process.
 * Sends start signals, stop signals, and sketches.
 * 
 */

public class Communicator {
	private PythonRunner runner;

	private StreamRedirectThread outThread;
	private MessageReceiverThread errThread;

	private PrintWriter toSketch;

	private Timer timer; //TODO

	public Communicator(Process sketchProcess, PythonRunner runner) {
		this.runner = runner;

		outThread = new StreamRedirectThread("JVM Stdout Reader", sketchProcess.getInputStream(), System.out);
		errThread = new MessageReceiverThread(sketchProcess.getErrorStream(), runner);

		toSketch = new PrintWriter(sketchProcess.getOutputStream());

		outThread.start();
		errThread.start();

		timer = new Timer();
	}

	public void destroy() {
		errThread.running = false;
		errThread = null;
		outThread = null;
		toSketch.close();
		toSketch = null;
	}

	/*
	 * Tell sketch to close
	 */
	public void sendClose() {
		toSketch.println("__STOP__"); //hard-coded, what the hell
		toSketch.flush();
	}

	/*
	 * Send a new sketch
	 */
	public void sendSketch(String[] args) {
		StringBuilder out = new StringBuilder("__SKETCH__");
		for (String a : args) {
			out.append(" " + a);
		}
		toSketch.println(out.toString());
		toSketch.flush();
	}

	//private class to handle doing things when the sketch process sends us a message via system.err
	private class MessageReceiverThread extends Thread {
		PythonRunner runner;
		BufferedReader messageReader;

		public boolean running;

		public MessageReceiverThread(InputStream messageStream, PythonRunner runner) {
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
						runner.parallelStopped();
					} else if (currentLine.indexOf("__STARTED__") != -1) {
						runner.parallelStarted();
					} else {
						System.err.println(currentLine);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
