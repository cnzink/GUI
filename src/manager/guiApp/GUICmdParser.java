/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager.guiApp;

import java.util.Vector;
import mesh.cmd.*;
import mesh.util.Converter;

/**
 *
 * @author Hang Yu
 */
class GUICmdParser {
    
    Vector<byte[]> message;
    
    public GUICmdParser()
    {
        message = new Vector<byte[]>();
    }
    void addCmd(byte[] nextMessage) {
        message.add(nextMessage);
    }

    Vector<GuiCmdInfo> getCmds() 
    {
        
        Vector<GuiCmdInfo> commands = new Vector<GuiCmdInfo>();
    
        for(int i = 0;i<message.size();i++)
        {
            byte[]info = message.get(i);
            
            int index = 0;
            char length = Converter.ByteArrayToChar(info, index);
            index+=2;
            
            while(index<length)
            {
                int cmdID = Converter.ByteArrayToChar(info, index);
                index+=2;
                int payloadLength = Converter.ByteArrayToChar(info, index);
                index+=2;
                String payload = "";
                if (payloadLength != 0)
                {
                    byte[] payloadContent = new byte[payloadLength];
                    for (int q = 0; q < payloadLength; q++)
                        payloadContent[q] = info[q + index];
                    payload = Converter.ByteArrayToHexString(payloadContent);
                    index += payloadLength;
                }
                
                switch(cmdID)
                {
                    case 0xFA01:
                        Cmd64001 cmd64001 = new Cmd64001(true);
                        cmd64001.parseRspPayload(payload);
                        commands.add(cmd64001);
                        break;
                    // get networkID    
                    case 0xFA06:
                        Cmd64006 cmd64006 = new Cmd64006(true);
                        cmd64006.ParseRspPayload(payload);
                        commands.add(cmd64006);
                        break;
                    // get channelMap
                    case 0xFA07:
                        Cmd64007 cmd64007 = new Cmd64007(true);
                        cmd64007.ParseRspPayload(payload);
                        commands.add(cmd64007);
                        break;
                        
                    // Set join key
                    case 0xFA08:
                        Cmd64008 cmd64008 = new Cmd64008(true);
                        cmd64008.ParseReqPayload(payload);
                        commands.add(cmd64008);
                        break;
                        
                    // get deviceList
                    case 0xFA09:
                        Cmd64009 cmd64009 = new Cmd64009(true);
                        cmd64009.ParseRspPayload(payload);
                        commands.add(cmd64009);
                        break;
                    // validate data format
                    case 0xFA0C:
                        Cmd64012 cmd64012 = new Cmd64012(true);
                        cmd64012.ParseRspPayload(payload);
                        commands.add(cmd64012);
                        break;
                    //Gui sent Core a list of device to update, Core responds with list of any rejected devices    
                    case 0xFA0D: //64013
                        Cmd64013 cmd64013 = new Cmd64013(true);
                        cmd64013.ParseRspPayload(payload);
                        commands.add(cmd64013);
                        break;
                    // 64015: display in send msg board    
                    case 0xFA0F:
                        Cmd64015 cmd64015 = new Cmd64015(true);
                        cmd64015.ParseReqPayload(payload);
                        commands.add(cmd64015);
                        break;
                    // 64016: display in recv msg board
                    case 0xFA10:  
                        Cmd64016 cmd64016 = new Cmd64016(true);
                        cmd64016.ParseReqPayload(payload);
                        commands.add(cmd64016);
                        break;
                    // 64017: mainframe repaint
                    case 0xFA11:   
                        Cmd64017 cmd64017 = new Cmd64017(true);
                        commands.add(cmd64017);
                        break;
                    // 64018: get manager about box version information
                    case 0xFA12:    
                        Cmd64018 cmd64018 = new Cmd64018(true);
                        cmd64018.ParseRspPayload(payload);
                        commands.add(cmd64018);
                        break;
                    // 64019: show gateway version
                    case 0xFA13:
                        Cmd64019 cmd64019 = new Cmd64019(true);
                        cmd64019.ParseReqPayload(payload);
                        commands.add(cmd64019);
                        break;
                   // 64020: show AP version
                    case 0xFA14:
                        Cmd64020 cmd64020 = new Cmd64020(true);
                        cmd64020.ParseReqPayload(payload);
                        commands.add(cmd64020);
                        break;
                   // 64021: update visualizer
                    case 0xFA15:
                        Cmd64021 cmd64021 = new Cmd64021(true);
                        cmd64021.ParseReqPayload(payload);
                        commands.add(cmd64021);
                        break;
                   // 64022: update node scan period
                    case 0xFA16:
                        Cmd64022 cmd64022 = new Cmd64022(true);
                        cmd64022.ParseReqPayload(payload);
                        commands.add(cmd64022);
                        break;
                   // 64023: remove edge
                    case 0xFA17:
                        Cmd64023 cmd64023 = new Cmd64023(true);
                        cmd64023.ParseReqPayload(payload);
                        commands.add(cmd64023);
                        break;
                   // 64024 add vertex (get graph)
                    case 0xFA18:
                        Cmd64024 cmd64024 = new Cmd64024(true);
                        cmd64024.ParseReqPayload(payload);
                        commands.add(cmd64024);
                        break;
                   // 64025 construct graph
                    case 0xFA19:
                        Cmd64025 cmd64025 = new Cmd64025(true);
                        commands.add(cmd64025);
                        break;
                   // 64026 add item to device white list
                    case 0xFA1A:
                        Cmd64026 cmd64026 = new Cmd64026(true);
                        cmd64026.ParseReqPayload(payload);
                        commands.add(cmd64026);
                        break;
                   // 64027 init schedule
                    case 0xFA1B:
                        Cmd64027 cmd64027 = new Cmd64027(true);
                        commands.add(cmd64027);
                        break;
                   // 64028 add edge
                    case 0xFA1C:
                        Cmd64028 cmd64028 = new Cmd64028(true);
                        cmd64028.ParseReqPayload(payload);
                        commands.add(cmd64028);
                        break;
                   // 64029 add edge
                    case 0xFA1D:
                        Cmd64029 cmd64029 = new Cmd64029(true);
                        cmd64029.ParseReqPayload(payload);
                        commands.add(cmd64029);
                        break;
                   // 64030 build from gateway graph
                    case 0xFA1E:
                        Cmd64030 cmd64030 = new Cmd64030(true);
                        cmd64030.ParseReqPayload(payload);
                        commands.add(cmd64030);
                        break;
                   // 64031 build from gateway graph (two nodes)
                    case 0xFA1F:
                        Cmd64031 cmd64031 = new Cmd64031(true);
                        cmd64031.ParseReqPayload(payload);
                        commands.add(cmd64031);
                        break;
                   
                    case 0xFA20:
                        Cmd64032 cmd64032 = new Cmd64032(true);
                        cmd64032.ParseRspPayload(payload);
                        commands.add(cmd64032);
                        break;
                        
                    case 0xFA21:
                        Cmd64033 cmd64033 = new Cmd64033(true);
                        cmd64033.ParseReqPayload(payload);
                        commands.add(cmd64033);
                        break;
                   
                    case 0xFA22:
                        Cmd64034 cmd64034 = new Cmd64034(true);
                        cmd64034.ParseReqPayload(payload);
                        commands.add(cmd64034);
                        break;
                    
                    case 0xFA27:
                        Cmd64039 cmd64039 = new Cmd64039(true);
                        cmd64039.ParseReqPayload(payload);;
                        commands.add(cmd64039);
                        break;
                        
                    case 0xFA2D:
                        Cmd64045 cmd64045 = new Cmd64045(true);
                        cmd64045.ParseReqPayload(payload);
                        commands.add(cmd64045);
                        break;
                        
                    case 64046: // FA2E
                        Cmd64046 cmd64046 = new Cmd64046(true);
                        cmd64046.ParseRspPayload(payload);
                        commands.add(cmd64046);
                        break;
                        
                      case 64047: // FA2F
                        Cmd64047 cmd64047 = new Cmd64047(true);
                        cmd64047.ParseReqPayload(payload);
                        commands.add(cmd64047);
                        break;  
                        
                    case 64613:
                        Cmd64613 cmd64613 = new Cmd64613(true);
                        cmd64613.ParseReqPayload(payload);
                        commands.add(cmd64613);
                        break;
                        
                }
                
            }
        } // end of while
        
        message.clear();
        
        return commands;
    }
    
}
