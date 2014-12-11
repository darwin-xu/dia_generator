package com.ericsson.sapc.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.sapc.tool.ConstantType.EVENT_TYPE;

public class BufferMgr {
	private static String PATTERN_EVENT		= "t_[a-z]*_[a-z]*_event";
	private static String PATTERN_FUNCTION	= "f_runEvent";
	
	private List<Event> events		= new LinkedList<Event>();
	private Set<String> nodeSet		= new HashSet<String>();
	private List<String> nodeList	= new LinkedList<String>();
	

	public void readInputFromFile(String fileName) {

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(fileName));
			String line;
			String eventType;
			Event event = new Event();
			boolean isSameFlow = true;

			int sequenceNumber = 0;
			while (bufferedReader.ready()) {
				line = bufferedReader.readLine();
				event.setNodeSeqence(sequenceNumber);
				if (!isSameFlow) {
					event = new Event();
				}
				if (null != line && !"".equals(line)) {
					// Get event list from input by reqex
					Pattern pattern = Pattern.compile(PATTERN_EVENT);
					Matcher matcher = pattern.matcher(line);
					if (matcher.find()) {
						eventType = matcher.group(0);
						event.setEventType(eventType);
						getEventInfo(line, event);
						isSameFlow = true;
					} else if (line.contains(PATTERN_FUNCTION)) { 
						// Get node list from input
						String nodeName = line.split("\\]")[1].split("\\[")[1];
						event.setNodeName(nodeName);
						nodeSet.add(nodeName);

						// Create a map to contain the node list and node
						// message
						events.add(event);
						isSameFlow = false;
						++sequenceNumber;
					}
				}
			}
			// Add SAPC node into the second position
			// Put this info into a list
			for (int i = 0; i < nodeSet.size(); ++i) {
				nodeList.add(i, nodeSet.toArray()[i].toString());
			}
			nodeList.add(1, "SAPC");
			for (int i = 0; i < nodeList.size(); ++i) {
				System.out.println(nodeList.toArray()[i]);
			}

			System.out.println("----------------------------------------");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != bufferedReader) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void getEventInfo(String line, Event event) {
		String release;
		String requestType;
		String eventType = event.getEventType();
		// Get the request type if the event is ccr event from Gx, Gxa, Sx
		if (EVENT_TYPE.GX_CCR_EVENT.toString().equals(eventType)
		 || EVENT_TYPE.GXA_CCR_EVENT.toString().equals(eventType)
		 || EVENT_TYPE.SX_CCR_EVENT.toString().equals(eventType)) 
		{
			release = line.split(",")[2];
			event.setRelease(release);
			requestType = line.split(",")[3].split("\\)")[0].trim();
			event.setRequestType(requestType);
		}
	}

//	*         SGSN_MME                     SAPC                        GGSN
//	*        ________                    ________                    ________
//	*       |        |                  |        |                  |        |
//	*       |        |                  |        |                  |        |
//	*       |        |                  |        |                  |        |
//	*       |        | (1) CCR-I        |        |                  |        |
//	*       |        | ---------------> |        |                  |        |
	public void showDiagramFromBuffer() {
		
		Diagram.showHeaderLine(nodeList);
		Diagram.showCommonLine(Diagram.COMMON.FIRST, nodeList);
		Diagram.showCommonLine(Diagram.COMMON.MIDDLE, nodeList);
		
		Iterator<Event> iter = events.iterator();
		while (iter.hasNext()) {
			Event event = (Event) iter.next();
			Diagram.showMessageLine(event, nodeList);
			

		}
		Diagram.showCommonLine(Diagram.COMMON.MIDDLE, nodeList);
		Diagram.showCommonLine(Diagram.COMMON.LAST, nodeList);
	}
	

}
