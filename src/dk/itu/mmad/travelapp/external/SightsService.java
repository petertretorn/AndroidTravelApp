// Solution by Peter Bruijn Larsen

package dk.itu.mmad.travelapp.external;

import java.util.List;

public interface SightsService {
	
	public String addSight(String body);
	
	public List<String> getSights(String location);
	
}
