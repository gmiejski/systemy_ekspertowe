import smile.Network;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        String path = "/home/marek/Dropbox/Studia/s9/Eksperty/medycyna_pracy_2.0.xdsl";
        Network network = new Network();
        network.readFile(path);
        List<Integer> leczId = Arrays.stream(network.getAllNodeIds())
                .filter(s -> s.startsWith("Leczenie"))
                .map(network::getNode)
                .collect(Collectors.toList());

        List<String> LeczNames = Arrays.stream(network.getAllNodeIds())
                .filter(s -> s.startsWith("Leczenie"))
                .map(network::getNodeName)
                .collect(Collectors.toList());

        System.out.println(leczId);
        System.out.println(LeczNames);
    }
}
