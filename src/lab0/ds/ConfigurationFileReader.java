package lab0.ds;

import java.io.*;
import java.util.*;

import javax.swing.JTextArea;

import org.yaml.snakeyaml.*;

public class ConfigurationFileReader {
	private HashMap<String, List<HashMap<String, String>>> parsedConfigFile;
	private ArrayList<Process> processes;
	private ArrayList<Rule> sendRules;
	private ArrayList<Rule> receiveRules;
	
	private JTextArea textArea;
	
	public ConfigurationFileReader(JTextArea textArea) {
		this.textArea = textArea; 
	}
	
	@SuppressWarnings("unchecked")
	public boolean parseFile(String fileName) {
		final Yaml yaml = new Yaml();
		Reader reader = null;
		try {
			reader = new FileReader(fileName);
			parsedConfigFile = (HashMap<String, List<HashMap<String, String>>>) yaml.load(reader);
			parseProcesses(parsedConfigFile);
			parseSendRules(parsedConfigFile);
			parseReceiveRules(parsedConfigFile);
			return true;
		} catch (final FileNotFoundException fnfe) {
			textArea.append("Yaml file read problem\n");
			return false;
		}
	}
	
	public ArrayList<Process> getProcesses() {
		return processes;
	}

	public ArrayList<Rule> getSendRules() {
		return sendRules;
	}

	public ArrayList<Rule> getReceiveRules() {
		return receiveRules;
	}
	
	public String getProcessNameByIp(String ipAddress) {
		for (Process process: processes) {
			if (process.getIpAddress().equals(ipAddress)) {
				return process.getName();
			}
		}
		return null;
	}
	
	public int getProcessPortByName(String name) {
		for (Process process: processes) {
			if (process.getName().equals(name)) {
				return process.getPort();
			}
		}
		return -1;
	}

	/* get process list from config file */
	private void parseProcesses(HashMap<String, List<HashMap<String, String>>> parsedConfigFile) {
		processes = new ArrayList<Process>();
		
		List<HashMap<String, String>> list = parsedConfigFile.get("configuration");
		if (list == null) {
			return ;
		}
		for (HashMap<String, String> i : list) {
			String name = i.get("name");
			String ipAddress = i.get("ip");
			int portNum = Integer.parseInt(String.valueOf(i.get("port")));
			processes.add(new Process(name, ipAddress, portNum));
		}
	}

	/* get send rules list from config file */
	private void parseSendRules(HashMap<String, List<HashMap<String, String>>> parsedConfigFile) {
		sendRules = new ArrayList<Rule>();
		
		List<HashMap<String, String>> sendRulesList = parsedConfigFile.get("sendRules");
		if (sendRulesList == null) {
			return ;
		}
		for (HashMap<String, String> i : sendRulesList) {
			/* set default value for every property if it is null */
			String action = i.get("action");
			if (action == null) {
				action = "";
			}
			String source = i.get("src");
			if (source == null) {
				source = "";
			}
			String destination = i.get("dest");
			if (destination == null) {
				destination = "";
			}
			String kind = i.get("kind");
			if (kind == null) {
				kind = "";
			}
			int sequenceNum;
			if (i.get("seqNum") == null) {
				sequenceNum = -1;
			} else {
				sequenceNum = Integer.parseInt(String.valueOf(i.get("seqNum")));
			}
			sendRules.add(new Rule(action, source, destination, kind, sequenceNum));
		}
	}
	
	/* get receive rules list from config file */
	private void parseReceiveRules(HashMap<String, List<HashMap<String, String>>> parsedConfigFile) {
		receiveRules = new ArrayList<Rule>();
		
		List<HashMap<String, String>> receiveRulesList = parsedConfigFile.get("receiveRules");
		if (receiveRulesList == null) {
			return;
		}
		for(HashMap<String, String> i : receiveRulesList) {
			/* set default value for every property if it is null */
			String action = i.get("action");
			if (action == null) {
				action = "";
			}
			String source = i.get("src");
			if (source == null) {
				source = "";
			}
			String destination = i.get("dest");
			if (destination == null) {
				destination = "";
			}
			String kind = i.get("kind");
			if (kind == null) {
				kind = "";
			}
			int sequenceNum;
			if (i.get("seqNum") == null) {
				sequenceNum = -1;
			} else {
				sequenceNum = Integer.parseInt(String.valueOf(i.get("seqNum")));
			}
			receiveRules.add(new Rule(action, source, destination, kind, sequenceNum));
		}
	}
}
