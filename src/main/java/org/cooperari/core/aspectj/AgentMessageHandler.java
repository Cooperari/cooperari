package org.cooperari.core.aspectj;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessage.Kind;
import org.aspectj.bridge.IMessageHandler;

/**
 * Message handler instantiated by the load-time weaving agent.
 * 
 * <p>
 * An instance of this object is created by the load-time weaver agent,
 * by setting the <code>-XmessageHandlerClass</code>
 * property to the name of this class, as described in the 
 * <a href="https://eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html">load-time weaving configuration for AspectJ</a>.
 * </p>
 * 
 * @since 0.2
 */
public class AgentMessageHandler implements IMessageHandler {

  /**
   * Constructs a new message handler.
   * The method calls {@link AgentFacade#signalActivation()} for {@link AgentFacade#INSTANCE}
   * to signal that the weaver agent is active.
   */
  public AgentMessageHandler() {
    AgentFacade.INSTANCE.signalActivation(); 
  }

  /**
   * Callback method to check for message types being ignored.
   * @param kind Message kind.
   * @return Always <code>false</code>, as the handler will take all messages.
   * @see IMessageHandler#isIgnoring(Kind)
   * 
   */
  @Override
  public boolean isIgnoring(Kind kind) {
    return false;
  }

  /**
   * Callback method.
   * @param kind Message kind.
   * @see IMessageHandler#ignore(Kind)
   */
  @Override
  public void ignore(Kind kind) {
    // Does nothing.
  }

  /**
   * Callback method.
   * @param kind Message kind.
   * @see IMessageHandler#dontIgnore(Kind)
   */
  @Override
  public void dontIgnore(Kind kind) {
    // Does nothing.
  }

  /**
   * Handle message.
   * The method redirects the message to {@link AgentFacade#INSTANCE} object
   * through {@link AgentFacade#handleMessage}.
   * @return <code>true</code> since the handler will handle all messages.
   * @see IMessageHandler#handleMessage(org.aspectj.bridge.IMessage)
   */
  @Override
  public boolean handleMessage(IMessage msg)  {
    AgentFacade.INSTANCE.handleMessage(msg);
    return true;
  }
}