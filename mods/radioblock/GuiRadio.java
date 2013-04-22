package mods.radioblock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiRadio extends GuiScreen{
	private TileEntityRadio radio;
	private GuiTextField streamTextBox;
	
	public GuiRadio(TileEntityRadio r){
		radio = r;
	}
	
	@Override
	public void initGui(){
		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, height / 2 + 10, 90, 20, "Play/Pause"));
		this.buttonList.add(new GuiButton(1, this.width / 2 + 10, height / 2 + 10, 90, 20, "Set Stream"));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 100, height / 2 + 60, 90, 20, "low"));
		this.buttonList.add(new GuiButton(3, this.width / 2 + 10, height / 2 + 60, 90, 20, "medium"));
		this.buttonList.add(new GuiButton(3, this.width / 2 + 100, height / 2 + 60, 30, 20, "hight"));
		streamTextBox = new GuiTextField(fontRenderer, width / 2 - 100, height / 2 + 35 , 200, 20);
		streamTextBox.setMaxStringLength(1000);
		streamTextBox.setText(radio.streamURL);
		Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	public void onGuiClosed(){
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3){
		streamTextBox.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	public void updateScreen(){
		streamTextBox.updateCursorCounter();
		if(radio.isInvalid()){
			this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
		}
	}
	
	@Override
	protected void keyTyped(char par1, int par2){
		streamTextBox.textboxKeyTyped(par1, par2);
		if (par1 == 13){
			actionPerformed((GuiButton)this.buttonList.get(1));
		}
		super.keyTyped(par1, par2);
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3){
		streamTextBox.mouseClicked(par1, par2, par3);
		super.mouseClicked(par1, par2, par3);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	 protected void actionPerformed(GuiButton par1GuiButton){
		if(par1GuiButton.id == 0){
			System.out.println(radio.isPlaying());
			/*if(radio.isPlaying()){
				radio.stopStream();
			}else{
				radio.startStream();
			}*/
			Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(ModRadioBlock.setPacket(radio.xCoord, radio.yCoord, radio.zCoord, radio.streamURL, !radio.isPlaying()));
		}
		if(par1GuiButton.id == 1){
			if(streamTextBox.getText().toLowerCase().endsWith(".m3u")){
				radio.streamURL = takeFirstEntryFromM3U(streamTextBox.getText());
			}else if(streamTextBox.getText().toLowerCase().endsWith(".pls")){
				radio.streamURL = parsePls(streamTextBox.getText());
			}else{
				radio.streamURL = streamTextBox.getText();
			}
			Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(ModRadioBlock.setPacket(radio.xCoord, radio.yCoord, radio.zCoord, radio.streamURL, radio.isPlaying()));
		}
		if(par1GuiButton.id == 2){
			if(radio.isPlaying()){
				System.out.println("Prej True");
			}else{
				System.out.println("Prej False");
		}
		}
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}
	
	public String takeFirstEntryFromM3U(String m3uurl){
		String out = "AudioNotFoundMaybeIShouldPutaPunchafaceSongHere";
		try {
			URL m3u = new URL(m3uurl);
			URLConnection con = m3u.openConnection();
			BufferedReader i = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String mp3;
			while((mp3 = i.readLine()) != null){
				if(!mp3.startsWith("#")){
					break;
				}
			}
			out = mp3;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	public String parsePls(String plsurl){
		String out = "AudioNotFoundMaybeIShouldPutaPunchafaceSongHere";
		try {
			URL pls = new URL(plsurl);
			URLConnection con = pls.openConnection();
			BufferedReader i = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String mp3;
			while((mp3 = i.readLine()) != null){
				String f = mp3.trim();
				if(f.contains("http://")){
					out = f.substring(f.indexOf("http://"));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
}
