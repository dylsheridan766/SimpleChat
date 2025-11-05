// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  private String LoginID; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI, String loginID) throws IOException 
   
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
	 this.LoginID=loginID;

    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if(message.startsWith("#")) {
    		handleCommand(message);
    	}
    	else
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  //performs operations given by input from the list of commands
  private void handleCommand(String command)  {
	  if(command.equals("#quit")) {
		  quit();
	  }
	  else if(command.equals("#logoff")) {
		  try {
			closeConnection();
		  } catch (IOException e) {}
	  }
	  else if(command.startsWith("#sethost")) {
		  if(!isConnected()) {
		  setHost(command.substring(9));
		  }
		  else
			  clientUI.display("You are currently logged in. You must log out to change the host");
	  }
	  else if(command.startsWith("#setport")) {
		  if(!isConnected()) {
			  setPort(Integer.parseInt(command.substring(9)));
			  }
			  else
				  clientUI.display("You are currently logged in. You must log out to change the port");
	  }
	  else if(command.equals("#login")) {
		  if(isConnected()) {
			  clientUI.display("You are currently logged in. You must log out to change the port");

		  } else
			try {
				openConnection();
			} catch (IOException e) {}
		  
	  }
	  else if(command.equals("#gethost")) {
		  clientUI.display("The host is: "+ getHost());
	  }
	  else if(command.equals("#getport")) {
		  clientUI.display("The port is: "+ getPort());
	  }
	  else
		  clientUI.display("Error this is not a valid command");
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
	protected void connectionException(Exception exception) {
	  clientUI.display("The server has shut down");
	  quit();
	}
  
  /**
	 * Implements the method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  @Override
	protected void connectionClosed() {
	  clientUI.display("Connection closed");
	}
  /**
	 * Implements the method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
  @Override
	protected void connectionEstablished() {
	  try {
		sendToServer("#login "+LoginID);
	  } catch (IOException e) {}
	}
}
//End of ChatClient class
