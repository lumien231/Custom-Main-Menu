package lumien.custommainmenu.lib.actions;

import lumien.custommainmenu.gui.GuiCustom;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ActionConnectToServer implements IAction
{
	String ip;
	String serverName;

	public ActionConnectToServer(String ip, String serverName)
	{
		this.ip = ip;
		this.serverName = serverName;
	}

	@Override
	public void perform(Object source, GuiCustom menu)
	{
		ServerData serverData = new ServerData(this.serverName, this.ip, false);
		FMLClientHandler.instance().setupServerList();
		FMLClientHandler.instance().connectToServer(menu, serverData);
	}
}
