/*
 * Confidential and proprietary. All rights reserved. Compact Solutions LLC 2010-2015.
 */

package controllers;

import smile.Network;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Net {
	private final Network network = new Network();

	public Net(String path) {
		network.setBayesianAlgorithm(Network.BayesianAlgorithmType.HeuristicImportance);
		network.readFile(path);
	}

	private Stream<Integer> treatments(){
		return Arrays.stream(network.getAllNodeIds())
				.filter(n -> n.startsWith("Leczenie"))
				.map(network::getNode);
	}

	private Stream<Integer> symptoms(){
		return Arrays.stream(network.getAllNodeIds())
				.filter(n -> n.startsWith("Symptom"))
				.map(network::getNode);
	}

	public void setEvidence(String[] appliedTreatments){
		Set<Integer> applied = Arrays.stream(appliedTreatments)
				.map(Integer::parseInt)
				.collect(Collectors.toSet());

		treatments()
				.filter(id -> applied.contains(id))
				.peek(id -> System.out.println("True: " + id + " " + network.getNodeName(id)))
				.forEach(node -> network.setEvidence(node, "True"));

		treatments()
				.filter(id -> !applied.contains(id))
				.peek(id -> System.out.println("False: " + id + " " + network.getNodeName(id)))
				.forEach(node -> network.setEvidence(node, "False"));
	}

	public String[][] getCuredSymptoms(){
		List<String> namesCol = symptoms().map(network::getNodeName).collect(Collectors.toList());
		String[] names = namesCol.toArray(new String[namesCol.size()]);

		List<String> probsCol = symptoms()
				.map(network::getNodeValue)
				.peek(a -> System.out.println(Arrays.toString(a)))
				.map(a -> a[1])
				.map(val -> val * 100)
				.sorted()
				.map(val -> String.format("%.2f%%", val))
				.collect(Collectors.toList());
		String[] probabilities = probsCol.toArray(new String[probsCol.size()]);

		return new String[][]{names, probabilities};
	}

	public void clearAllEvidence() {
		network.clearAllEvidence();
	}

	public void updateBeliefs() {
		network.updateBeliefs();
	}
}
