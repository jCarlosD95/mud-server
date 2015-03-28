package mud.utils;

import java.util.*;

import mud.MUDServer;
import mud.objects.Player;

public class ChatChanneler
{
	final private MUDServer mud;
	private HashMap<String, ChatChannel> channels = new HashMap<String, ChatChannel>();

	public ChatChanneler(final MUDServer mud) {
		this.mud = mud;
	}

    public List<Player> getListeners(final String channelName) {
    	return channels.get(channelName).getListeners();
    }
    
    public ChatChannel getChatChannel(final String channelName) {
    	final ChatChannel channel = this.channels.get(channelName);
    	
    	if( channel == null ) {
    		for(final ChatChannel ch : this.channels.values()) {
    			if( ch.getShortName().equals(channelName) ) {
    				return ch;
    			}
    		}
    	}
    	
    	return channel;
    	//return this.channels.get(channelName);
    }
    
    public Collection<ChatChannel> getChatChannels() {
    	return channels.values();
    }

    public Collection<String> getChannelNames() {
    	return new ArrayList<String>(channels.keySet());
    }

	public boolean hasChannel(final String channelName) {
		return channels.containsKey(channelName);
	}

	public boolean isPlayerListening(final String channelName, final Player player) {
		return channels.containsKey(channelName) && channels.get(channelName).isListener(player);
	}

	public void makeChannel(final String channelName) {
		if (!channels.containsKey(channelName)) {
			channels.put(channelName, new ChatChannel(channelName));
		}
	}
	
	/**
	 * 
	 * @param player
	 * @param channelName
	 * @return
	 * @throws Exception
	 */
	public boolean add(final Player player, final String channelName) throws Exception {
		if (!channels.containsKey(channelName)) {
            throw new Exception("No channel by the name of " + channelName);
        }
		
		ChatChannel channel = channels.get(channelName);
		
		if( player.getAccess() >= channel.getRestrict() ) {
			channels.get(channelName).addListener(player);
			return true;
		}
		
		return false;
	}

	public void remove(final Player player, final String channelName) {
		if (channels.containsKey(channelName)) {
            channels.get(channelName).removeListener(player);
        }
	}

	public void send(final String channelName, final Player player, final String message) {
		ChatChannel chan = getChatChannel(channelName);
		
		if (chan != null) {
			chan.write(player, message); // add message to ChatChannel message queue
            mud.debug("(" + channelName + ") <" + player.getName() + "> " + message + "\n");										
        }
	}

	public void send(final String channelName, final String message) {
		ChatChannel chan = getChatChannel(channelName);

		if (chan != null) {
			chan.write(message); // add message to ChatChannel message queue
			mud.debug("(" + channelName + ") " + message + "\n");
		}
	}
	
	//String name = chan.getName(), chan_color = chan.getChanColor(), text_color = chan.getTextColor();
    //player.getClient().write("(" + mud.colors(name, chan_color) + ") " + "<" + player.getName() + "> " + mud.colors(message, text_color) + "\r\n");
	
	//String name = chan.getName(), chan_color = chan.getChanColor(), text_color = chan.getTextColor();
	
	/*for (final Player player : channels.get(channelName).getListeners()) {
        player.getClient().write("(" + mud.colors(name, chan_color) + ") " + mud.colors(message, text_color) + "\r\n");										
    }*/
}