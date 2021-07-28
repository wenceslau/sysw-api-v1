package com.suite.core.util;

import com.suite.security.cryptography.RSATransporter;

public class Main {

	public static String toHexString(byte[] ba) {

		StringBuilder str = new StringBuilder();
		for (int i = 0; i < ba.length; i++)
			str.append(String.format("%x", ba[i]));
		return str.toString();

	}

	public static String fromHexString(String hex) {

		StringBuilder str = new StringBuilder();

		for (int i = 0; i < hex.length(); i += 2) {

			str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));

		}

		return str.toString();

	}
	
	public static void main(String[] args) throws Exception {
		

		
		
		RSATransporter rsa = new RSATransporter();
		System.out.println(rsa.encrypt("UoT9P9MMqolMoE36pZHh"));

//		ScriptEngineManager mgr = new ScriptEngineManager();
//	    ScriptEngine engine = mgr.getEngineByName("JavaScript");
//	    String foo = "10*4100.9";
//	    System.out.println(engine.eval(foo));
		
		//String generatedString = RandomStringUtils.randomAlphabetic(15);
		//System.out.println(generatedString.toUpperCase());
		
//		 	Connection conn = null;
//	        ResultSet rs = null;
//	       // String url = "jdbc:jtds:sqlserver://128.0.0.1;DatabaseName=master;loginTimeout=10;socketTimeout=1";
//	      //  String driver = "net.sourceforge.jtds.jdbc.Driver";
//	        
//	      
//	        Socket pingSocket = null;
//	        PrintWriter out = null;
//	        BufferedReader in = null;
//
//	        try {
//	            pingSocket = new Socket("127.0.0.1", 1433);
//	            out = new PrintWriter(pingSocket.getOutputStream(), true);
//	            in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//
//	        out.println("ping");
//	        System.out.println(in.readLine());
//	        out.close();
//	        in.close();
//	        pingSocket.close();
		
		
		//String value = String.format("%tT", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("13:01:01"));
		
		//System.out.println(value);
		
//		Map<String, Object> mp = new HashMap<>();
//
//		mp.put("ID", 100L );
//		
//		System.out.println(mp);
//		
//		mp.put("ID", 500L );
//		
//		System.out.println(mp);

//		
//		String _5 = StringUtils.z("{\"businesUniqueId\":\"XXXXXXX\",\"appLicences\":[{\"machineInfo\":null,\"applicationName\":\"App1\",\"applicationUniqueId\":\"YYYYYYYYYYYYY\",\"expiration\":\"2020-05-13\",\"licenceNumber\":0},{\"machineInfo\":null,\"applicationName\":\"App2\",\"applicationUniqueId\":\"YYYYYYYYYYYYY\",\"expiration\":\"2020-05-13\",\"licenceNumber\":0}]}");
//		System.out.println(_5);
//		String _7 = StringUtils.dec(_5);
//		System.out.println(_7);

		// /* Total number of processors or cores available to the JVM */
		// System.out.println("Available processors (cores): " +
		// Runtime.getRuntime().availableProcessors());
		//
		// /* Total amount of free memory available to the JVM */
		// System.out.println("Free memory (bytes): " +
		// Runtime.getRuntime().freeMemory());
		//
		// /* This will return Long.MAX_VALUE if there is no preset limit */
		// long maxMemory = Runtime.getRuntime().maxMemory();
		// /* Maximum amount of memory the JVM will attempt to use */
		// System.out.println("Maximum memory (bytes): " +
		// (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));
		//
		// /* Total memory currently available to the JVM */
		// System.out.println("Total memory available to JVM (bytes): " +
		// Runtime.getRuntime().totalMemory());
		//
		// /* Get a list of all filesystem roots on this system */
		// File[] roots = File.listRoots();
		//
		// /* For each filesystem root, print some info */
		// for (File root : roots) {
		// System.out.println("File system root: " + root.getAbsolutePath());
		// System.out.println("Total space (bytes): " + root.getTotalSpace());
		// System.out.println("Free space (bytes): " + root.getFreeSpace());
		// System.out.println("Usable space (bytes): " + root.getUsableSpace());
		// }
		//

		// SystemInfo si = new SystemInfo();
		// System.out.println(si.getOperatingSystem().getBitness());
		// System.out.println(si.getOperatingSystem().getFamily());
		// System.out.println(si.getOperatingSystem().getManufacturer());
		// System.out.println(si.getOperatingSystem().getProcessCount());
		// System.out.println(si.getOperatingSystem().getProcessId());
		// System.out.println(si.getOperatingSystem().getSystemBootTime());
		// System.out.println(si.getOperatingSystem().getSystemUptime());
		// System.out.println(si.getOperatingSystem().getThreadCount());
		// System.out.println(si.getOperatingSystem().getFileSystem().getFileStores());
		// System.out.println(si.getOperatingSystem().getNetworkParams().getDomainName());
		// System.out.println(si.getOperatingSystem().getNetworkParams().getHostName());
		// System.out.println(si.getOperatingSystem().getNetworkParams().getIpv4DefaultGateway());
		// System.out.println(si.getOperatingSystem().getNetworkParams().getIpv6DefaultGateway());
		// System.out.println(si.getOperatingSystem().getNetworkParams().getDnsServers());
		// System.out.println(si.getOperatingSystem().getVersionInfo().getBuildNumber());
		// System.out.println(si.getOperatingSystem().getVersionInfo().getCodeName());
		// System.out.println(si.getOperatingSystem().getVersionInfo().getVersion());
		// System.out.println(si.getHardware().getComputerSystem().getManufacturer());
		// System.out.println(si.getHardware().getComputerSystem().getModel());
		// System.out.println(si.getHardware().getComputerSystem().getSerialNumber());
		// System.out.println(si.getHardware().getComputerSystem().getBaseboard().getSerialNumber());
		// System.out.println(si.getHardware().getComputerSystem().getFirmware().getReleaseDate());

		//
		// System.out.println(si.getOperatingSystem().getBitness());
		//
		//
		//
		// HardwareAbstractionLayer hal = si.getHardware();
		// long availableMemory = hal.getMemory().getAvailable();

		// System.out.println(availableMemory);

		// int i = 547;
		//
		// if (i >= 547)
		// System.out.println("maior = 547");
		//
		// if (i < 548)
		// System.out.println("menor = 548");
		//
		//
		// if (i >=547 && i <=548)
		// System.out.println("true");
		// else
		// System.out.println("false");
		//
		//// String t = "báçáàâãéèêíìîóòôõúùû&";
		////
		//// if (Utils.hasSpecialCharacter(t)) {
		//// System.out.println("pegou");
		//// }
		////
		//// if (Utils.hasSpecialCharacter(Utils.deAccent(t))) {
		//// System.out.println("pegou");
		//// }
		////
		////
		//// System.out.println(Utils.deAccent(t));
		//
		//// Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		//// Matcher m = p.matcher("I am a' string");
		//// boolean b = m.find();
		////
		//// if (b)
		//// System.out.println("There is a special character in my string !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
		////
		////
		//// String s = " ";
		////
		//// System.out.println("["+s.trim()+"]");
		//
		//// System.out.println(toHexString("master".getBytes()));
		////
		// //System.out.println(fromHexString("5a69756c32353036"));
		////
		//
		//
		////
		//// String val = "_INDICADORES____VENCIDOS_SIM_";
		////
		//// val = val.toLowerCase();
		//// char[] chars = val.toCharArray();
		////
		//// for (int i = 0; i < chars.length; i++) {
		//// //Pega o char e compara com o _
		//// char c = chars[i];
		//// if (c == '_') {
		//// //A posicao do _ nao pode ser a ultima da string
		//// if (i != val.length() -1) {
		//// //Pega o char posterior ao _ e capitaliza ele
		//// String strChar = String.valueOf(chars[i+1]);
		//// chars[i+1] = strChar.toUpperCase().charAt(0);
		//// }
		//// }
		//// }
		//// //Cria a string com os chars remove o underline e capitaliza e primeira letra
		//// val = new String(chars);
		//// val = val.replace("_", "");
		//// val = Character.toUpperCase(val.charAt(0)) + val.substring(1);
		////
		////
		//// System.out.println(val);
	}

}
