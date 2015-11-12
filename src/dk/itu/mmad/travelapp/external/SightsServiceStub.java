// Solution by Peter Bruijn Larsen

package dk.itu.mmad.travelapp.external;

import java.util.ArrayList;
// Solution by Peter Bruijn Larsen

import java.util.List;

public class SightsServiceStub implements SightsService {

	@Override
	public String addSight(String body) {

		return "Submission succesfull";
	}

	@Override
	public List<String> getSights(String location) {

		List<String> list = new ArrayList<String>();

		list.add("Cathedral: For praying " + "\nUploder: Peter");
		list.add("Bazar: For shopping " + "\nUploder: Peter");
		list.add("Beach: For fun " + "\nUploder: Peter");

		return list;
		}
}
