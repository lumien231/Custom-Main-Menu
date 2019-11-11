package lumien.custommainmenu.lib.actions;

import lumien.custommainmenu.gui.GuiCustom;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ActionConnectToServer implements IAction
{
	String ip;
	String serverName;

	public ActionConnectToServer(String ip)
	{
		this.ip = ip;
	}

	@Override
	public void perform(Object source, GuiCustom menu)
	{
		ServerData serverData = new ServerData(null, ip, false);
		FMLClientHandler.instance().setupServerList();
		FMLClientHandler.instance().connectToServer(menu, serverData);
	}
}
