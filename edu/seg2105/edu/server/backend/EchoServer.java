package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    //create server consol instance
    ServerConsole consol = new ServerConsole(sv);
    
    try 
    {
      sv.listen(); //Start listening for connections
      consol.accept();
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
  
 public static class ServerConsole implements ChatIF{
	 private EchoServer server;
	 //constructor
	 public ServerConsole(EchoServer server) {
	        this.server = server;
	    }
	 public void accept() 
	  {
	    try
	    {
	    BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
	      String message;

	      while (true) 
	      {
	        message = fromConsole.readLine();
	        this.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("SERVER MSG>" + "Unexpected error while reading from console!" );
	    }
	  }
	 public void handleMessageFromServerUI(String message) {
	
			 if(message.startsWith("#")) {
		    		handleCommand(message);
		    	}
		    	else
			 display("SERVER MSG> "+ message);
			 server.sendToAllClients("SERVER MSG> "+ message);

		 
	 }
	@Override
	public void display(String message) {
		System.out.println(message);
	}
	private void handleCommand(String command)  {
		  if(command.equals("#quit")) {
			  System.exit(0);
		  }
		  else if (command.equals("#stop")) {
			  server.stopListening();
		  }
		  else if (command.equals("#close")) {
			  try {
				server.close();
			  } catch (IOException e) {}
		  }
		  else if (command.startsWith("#setport")) {
			  	if(!server.isListening()&& server.getNumberOfClients()==0) {
			  		server.setPort(Integer.parseInt(command.substring(9)));			  		
			  	}
			  	else
			  		System.out.println("Error The server must be closed to chnage the port");
		  }
		  else if (command.equals("#start")) {
			  if(server.isListening()) {
			  		System.out.println("Error The server must be be stopped to be able to start");
			  } else
				try {
					server.listen();
				} catch (IOException e) {}
		  }
		  else if (command.equals("#getport")) {
			  System.out.println(server.getPort());
		  }
	 
 }
 }
  
  @Override
	protected void clientConnected(ConnectionToClient client) {
//prints a message when a client connects
  	System.out.println("A client has connected to the server!");  
  }
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  	System.out.println("A client has disconnected to the server!");  

	  // Since we don't track which ID belongs to this client directly,
		// remove by value.
	  	super.clientDisconnected(client);
	}
}
//End of EchoServer class
