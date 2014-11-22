package me.jezza.oc.api.interfaces;

import me.jezza.oc.api.NetworkResponse.MessageResponse;

/**
 * Used to solely define a message.
 * Gives a proper interface for the system to pass through simply.
 * <p/>
 * The basic idea of a message is the container of information.
 * Once posted the message will be run through the processing system delivering it to all nearby nodes connected.
 * This implementation can vary.
 * For instance, in {@link me.jezza.oc.api.NetworkCore}, I run it through a breadth-first search, resulting in a propagation.
 * <p/>
 */
public interface INetworkMessage {

    /**
     * Should be set upon creation.
     *
     * @param node the message being passed ownership.
     */
    public void setOwner(INetworkNode node);

    /**
     * @return current INetworkNode that is viewed as the node that sent out this message.
     */
    public INetworkNode getOwner();

    /**
     * Called when the message gets reposted upon an invalid network traversal.
     * This is determined by the owner node.
     * <p/>
     * Use this to reset any values before the repost.
     */
    public void resetMessage();

    /**
     * Fired for the use of the message.
     * If the message wants to add it to a list, or alter something, you have the ability to.
     *
     * @param node The node being processed.
     */
    public MessageResponse isValidNode(INetworkNode node);

}
