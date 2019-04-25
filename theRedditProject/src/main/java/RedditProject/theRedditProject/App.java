package RedditProject.theRedditProject;

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.pagination.Paginator;
import net.dean.jraw.references.SubredditReference;


import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.*;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.*;


public class App 
{
	
	static int DEFAULT_DEPTH = 10;
	

	
    public static void main( String[] args ) throws IOException
    {
    	
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter URL exactly as it appears in browser (Copy and Paste it!)");
        String userInput = myObj.nextLine();
    	
    	Document doc = Jsoup.connect("https://www.reddit.com/api/info.json?url=" + userInput).ignoreContentType(true).get();
    	Element body = doc.body();
    	String bodyText = body.text();


    	if (bodyText.toLowerCase().contains("children\": []")) {
    		System.out.println("That URL has not been posted to Reddit before. Try another!");
    	}
    	
    	else {
    		
    		int searchDegree = 0;
    		int limit = 0;
    		int numSubReddits = 0;
    		String findStr = "subreddit\": \"";
    		int lastIndex = 0;
    		while(lastIndex != -1){

    		    lastIndex = bodyText.indexOf(findStr,lastIndex);

    		    if(lastIndex != -1){
    		        numSubReddits++;
    		        lastIndex += findStr.length();
    		    }
    		}
    		
    		int i = 0;
    		i = bodyText.indexOf("subreddit\": ", 0);
    		
    		
    		String[] splitText = bodyText.split(" ");

    		Vector<String> subreddits_string = new Vector<String>();
    		Vector<String> subreddits_subs = new Vector<String>();
    		Vector<String> subreddits_scores = new Vector<String>();
    		Vector<String> subreddits_comments_string = new Vector<String>();
    		for (int a = 0; a < splitText.length; a++) {
    			if (splitText[a].contains("\"subreddit\":")) {
    				subreddits_string.add(splitText[a+1]);
    			}
    			else if (splitText[a].contains("\"subreddit_subscribers\":")) subreddits_subs.add(splitText[a+1]);
    			else if (splitText[a].contains("\"score\":")) subreddits_scores.add(splitText[a+1]);
    			else if (splitText[a].contains("\"num_comments\":")) subreddits_comments_string.add(splitText[a+1]);
    		}
    		
    		for (int c = 0; c < subreddits_subs.size(); c++) {
    			subreddits_subs.set(c, subreddits_subs.get(c).replace(",", ""));
    		}
    		
    		for (int c = 0; c < subreddits_scores.size(); c++) { 
    			subreddits_scores.set(c, subreddits_scores.get(c).replace(",", ""));
    		}
    		
    		for (int c = 0; c < subreddits_comments_string.size(); c++) {
    			subreddits_comments_string.set(c, subreddits_comments_string.get(c).replace(",", ""));
    		}
    		
    		Vector<Integer> subreddits_comments = new Vector<Integer>();
    		
    		for (int c = 0; c < subreddits_comments_string.size(); c++) {
    			subreddits_comments.add(Integer.parseInt(subreddits_comments_string.get(c)));
    		}
    		
    		for (int c = 0; c < subreddits_string.size(); c++) {
    			String temp = subreddits_string.get(c);
    			temp = temp.replaceFirst("\"", "");
    			temp = temp.replace("\",", "");
    			subreddits_string.set(c, temp);
    		}
    		
    		for (int x = 0; x < subreddits_string.size(); x++) System.out.println(subreddits_string.get(x) + ": " + subreddits_subs.get(x) + " (" + subreddits_scores.get(x) + ")  " + subreddits_comments.get(x));
    		
    		System.out.println("Enter a search degree for related subreddits (Suggested 0,5,10):");
    		
    		searchDegree = Integer.parseInt(myObj.nextLine());
    		
    		System.out.println("How would you like relevancy calculated by? (s for score, c for comments, b for both)");
            char eq = myObj.next().charAt(0);
            
            System.out.println("How many subreddits would you like returned?");
//            limit = Integer.parseInt(myObj.nextLine());
            limit = myObj.nextInt();
    		
    		Vector<Double> subreddits_relevancy = new Vector<Double>();
    		
    		if (eq == 's') for (int x = 0; x < subreddits_string.size(); x++) subreddits_relevancy.add(Double.parseDouble(subreddits_scores.get(x))/Double.parseDouble(subreddits_subs.get(x)));
    		else if (eq == 'c') {
    			for (int x = 0; x < subreddits_string.size(); x++) subreddits_relevancy.add((double)subreddits_comments.get(x)/Double.parseDouble(subreddits_subs.get(x)));
    		}
    		else if (eq == 'b') for (int x = 0; x < subreddits_string.size(); x++) subreddits_relevancy.add((Double.parseDouble(subreddits_scores.get(x)) + (double)subreddits_comments.get(x))/Double.parseDouble(subreddits_subs.get(x)));
    		for (int x = 0; x < subreddits_relevancy.size(); x++) System.out.println(1/subreddits_relevancy.get(x));
    		
    		

    		UserAgent userAgent = new UserAgent("java:RedditProject:v1.0 (by /u/cs491smda)");
    		net.dean.jraw.oauth.Credentials credentials = net.dean.jraw.oauth.Credentials.script("cs491smda", "rolltide",
    			    "H-7P-YfqSIQP-g", "ApF7ePL2psbIV1scnMc5gGGlNLU");
    		NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
    		RedditClient reddit = net.dean.jraw.oauth.OAuthHelper.automatic(adapter, credentials);

    		Subreddit sr = reddit.subreddit("RocketLeague").about();
    		//System.out.println(sr.toString());
    		System.out.println(sr.getSubscribers());
    		Vector<SubredditReference> subreddits_reference = new Vector<SubredditReference>();
    		for (int x = 0; x < subreddits_string.size(); x++) subreddits_reference.add(reddit.subreddit(subreddits_string.get(x)));
    		
    		for (int x = 0; x < subreddits_reference.size(); x++) System.out.println(subreddits_reference.get(x).getSubreddit());
    		
    		
    		/*
    		DefaultPaginator<Submission> paginator = subreddits_reference.get(0).posts()
    				.limit(Paginator.RECOMMENDED_MAX_LIMIT)
    				.sorting(SubredditSort.TOP)
    				.timePeriod(TimePeriod.ALL)
    				.build();
    		
    		
    		Listing<Submission> firstPage = paginator.next();
    		
    		*/
    		
    		System.out.println("FLAGFLAGFLAG");
    		
    		
    		Vector<subreddit_top_posts> total_subreddit_list = new Vector<subreddit_top_posts>();
    		
    		for (int x = 0; x < subreddits_reference.size(); x++) {
    			
//    			System.out.println(subreddits_reference.get(x).getSubreddit());
    			
    			DefaultPaginator<Submission> paginator = subreddits_reference.get(x).posts()
        				.limit(Paginator.RECOMMENDED_MAX_LIMIT)
        				.sorting(SubredditSort.TOP)
        				.timePeriod(TimePeriod.ALL)
        				.build();
    			
    			Listing<Submission> firstPage = paginator.next();
    			
				subreddit_top_posts temp = new subreddit_top_posts();
				temp.name = subreddits_reference.get(x).getSubreddit();
				temp.subscribers = subreddits_reference.get(x).about().getSubscribers();
//    			for (int y = 0; y < 10; y++) if (firstPage.get(y) != null) System.out.println(firstPage.get(y).getUrl());
    			for (int y = 0; (y < DEFAULT_DEPTH) && (y < firstPage.size()); y++) {
    				if (firstPage.get(y) != null) {
    					temp.urls.add(firstPage.get(y).getUrl());
    					temp.scores.add(firstPage.get(y).getScore());
    					temp.numComments.add(firstPage.get(y).getCommentCount());
    				}
    			}
    				
    			total_subreddit_list.add(temp);
    			
    			
    			
    			//for (int y = 0; y < 10; y++) if (firstPage. != null) total_subreddit_list.add(
    			
    			
    		}
    		
    		System.out.println("GALFGALFGALF");
    		
//    		System.out.println(total_subreddit_list.size());
//    		for (int x = 0; x < total_subreddit_list.size(); x++) {
//    			System.out.println(total_subreddit_list.get(x).name + " with " + total_subreddit_list.get(x).subscribers + " subscribers");
//    			for (int y = 0; y < total_subreddit_list.get(x).urls.size(); y++) {
//    				System.out.println(total_subreddit_list.get(x).urls.get(y));
//    				System.out.println(total_subreddit_list.get(x).numComments.get(y));
//    			}
//    		}
    		
    		
    		
    		/*
    		for (Submission post : firstPage) {
    			System.out.println(String.format("%s (/r/%s, %s points) - %s",
                        post.getTitle(), post.getSubreddit(), post.getScore(), post.getUrl()));
    		}
    		*/
    		
    		
    		Vector<String> old_subreddits_string = new Vector<String>();
    		
    		
    		//Construct graph
    		
    		 System.out.println("here");
    		
    		 Graph<String, DefaultEdge> graph = new DirectedWeightedPseudograph<String, DefaultEdge>(DefaultEdge.class);
    		 
    		 Vector<String> names = new Vector<String>();
    		 
    		 String origin = "origin";
    		 graph.addVertex(origin);
    		 
    		 for (int a=0; a<subreddits_string.size(); a++) {
//    		 for (int a = 0; a < total_subreddit_list.size(); a++) {
    			 if (names.contains(subreddits_string.get(a)) == false) {
    				 old_subreddits_string.add(subreddits_string.get(a));
    				 graph.addVertex(subreddits_string.get(a));
    				 names.add(subreddits_string.get(a));
    			 }
    			 graph.addEdge(origin, subreddits_string.get(a));
    			 
 //   			 if (subreddits_relevancy.get(a))
    			 
    			 graph.setEdgeWeight(origin, subreddits_string.get(a), 1/(double)subreddits_relevancy.get(a)); //Changed to cast as a double
    		 }
    		 
    		 System.out.println("OLD IS SIZE " + old_subreddits_string.size() + " AND TOTAL IS SIZE " + total_subreddit_list.size());
    		 
    		 for (int x=0; x<total_subreddit_list.size(); x++) {
//    			 System.out.println("\n\n\n\n" + total_subreddit_list.get(x).name + "\n\n\n\n");
    			 for (int y=0; y<total_subreddit_list.get(x).urls.size(); y++) {
    				 doc = Jsoup.connect("https://www.reddit.com/api/info.json?url=" + total_subreddit_list.get(x).urls.get(y)).ignoreContentType(true).get();
    			     body = doc.body();
    			     bodyText = body.text();
    			     
    			     if (bodyText.toLowerCase().contains("children\": []")) {
    			    	 	break;
    			    	}
    			     else {
    			    	 numSubReddits = 0;
    			    		findStr = "subreddit\": \"";
    			    		lastIndex = 0;
    			    		while(lastIndex != -1){

    			    		    lastIndex = bodyText.indexOf(findStr,lastIndex);

    			    		    if(lastIndex != -1){
    			    		        numSubReddits++;
    			    		        lastIndex += findStr.length();
    			    		    }
    			    		}

    			    		i = 0;
    			    		i = bodyText.indexOf("subreddit\": ", 0);


    			    		splitText = bodyText.split(" ");
    			    		
    			    		subreddits_string = new Vector<String>();
    			    		subreddits_subs = new Vector<String>();
    			    		subreddits_scores = new Vector<String>();
    			    		subreddits_comments_string = new Vector<String>();
    			    		
    			    		for (int a = 0; a < splitText.length; a++) {
    			    			if (splitText[a].contains("\"subreddit\":")) {
    			    				subreddits_string.add(splitText[a+1]);
    			    			}
    			    			else if (splitText[a].contains("\"subreddit_subscribers\":")) subreddits_subs.add(splitText[a+1]);
    			    			else if (splitText[a].contains("\"score\":")) subreddits_scores.add(splitText[a+1]);
    			    			else if (splitText[a].contains("\"num_comments\":")) subreddits_comments_string.add(splitText[a+1]);
    			    		}

    			    		for (int c = 0; c < subreddits_subs.size(); c++) {
    			    			subreddits_subs.set(c, subreddits_subs.get(c).replace(",", ""));
    			    		}

    			    		for (int c = 0; c < subreddits_scores.size(); c++) {
    			    			subreddits_scores.set(c, subreddits_scores.get(c).replace(",", ""));
    			    		}
    			    		
    			    		for (int c = 0; c < subreddits_comments_string.size(); c++) {
    			    			subreddits_comments_string.set(c, subreddits_comments_string.get(c).replace(",", ""));
    			    		}
    			    		
    			    		subreddits_comments = new Vector<Integer>();
    			    		
    			    		for (int c = 0; c < subreddits_comments_string.size(); c++) {
    			    			subreddits_comments.add(Integer.parseInt(subreddits_comments_string.get(c)));
    			    		}

    			    		for (int c = 0; c < subreddits_string.size(); c++) {
    			    			String temp = subreddits_string.get(c);
    			    			temp = temp.replaceFirst("\"", "");
    			    			temp = temp.replace("\",", "");
    			    			subreddits_string.set(c, temp);
    			    		}
    			    		
    			    		String parent = total_subreddit_list.get(x).name;
    			    		
    			    		for (int a=0; a<subreddits_string.size(); a++) {
//    			    			if (parent != subreddits_string.get(a)){
	    			    			if (names.contains(subreddits_string.get(a)) == false) {
	    			    				graph.addVertex(subreddits_string.get(a));
	    			    				names.add(subreddits_string.get(a));
	    			    			}
	    			    			 graph.addEdge(parent, subreddits_string.get(a));
	    			    			 
	    			    			 System.out.println("Size of vectors: "
	    			    					 + subreddits_scores.size() + " , "
	    			    					 + subreddits_comments.size() + " , "
	    			    					 + subreddits_subs.size()
	    			    			 );
	    			    					 
	    			    					 
	    			    					 
	    			    					 
	    			    			 
	    			    			 System.out.println(
	    			    				"Score: " + Double.parseDouble(subreddits_scores.get(a)) + " | " +
	    			    				"Comments: " + (double)subreddits_comments.get(a) + " | " +
	    			    				"Subs: " + Double.parseDouble(subreddits_subs.get(a)) + " | " +
	    			    				"Edge weight: " + 1/((Double.parseDouble(subreddits_scores.get(a)) + (double)subreddits_comments.get(a))/Double.parseDouble(subreddits_subs.get(a)))
	    			    			);
	    			    			 
	    			    			 
	    			    			 
	    			    			 if (eq == 's') {
	    			    				 
	    			    				 if (Double.parseDouble(subreddits_subs.get(a)) < 50 || Double.parseDouble(subreddits_scores.get(a)) <= 2) {
	    			    					 graph.setEdgeWeight(parent, subreddits_string.get(a), 999999999);
	    			    				 }
	    			    				 else {
	    			    					 graph.setEdgeWeight(parent, subreddits_string.get(a),
	    			    					 1/
	    			    					 ((Double.parseDouble(subreddits_scores.get(a)))/(Double.parseDouble(subreddits_subs.get(a))))
	    			    					 );
	    			    				 }
	    			    				 
	    			    			 }
	    			    			 else if (eq == 'c') {
	    			    				 
	    			    				 if (Double.parseDouble(subreddits_subs.get(a)) < 50 || Double.parseDouble(subreddits_scores.get(a)) <= 2 || (double)subreddits_comments.get(a) < 1) {
	    			    					 graph.setEdgeWeight(parent, subreddits_string.get(a), 999999999);
	    			    				 }
	    			    				 else {
	    			    					 graph.setEdgeWeight(parent, subreddits_string.get(a), 1/((double)subreddits_comments.get(a)/Double.parseDouble(subreddits_subs.get(a))));
	    			    				 }
	    			    		     }
	    			    			 else if (eq == 'b') {
	    			    				 
	    			    				 if (Double.parseDouble(subreddits_subs.get(a)) < 50 || Double.parseDouble(subreddits_scores.get(a)) <= 2) {
	        			    				 graph.setEdgeWeight(parent, subreddits_string.get(a), 999999999);
	    			    				 }
	    			    				 else {
	//    			    				 	graph.setEdgeWeight(origin, subreddits_string.get(a), 1/ ((Double.parseDouble(subreddits_scores.get(a)) + subreddits_comments.get(x))));
	    			    					 graph.setEdgeWeight(parent, subreddits_string.get(a), 1/((Double.parseDouble(subreddits_scores.get(a)) + (double)subreddits_comments.get(a))/Double.parseDouble(subreddits_subs.get(a))));
	    			    				 }
	    			    			 }
//    			    			}
    			    		}
    			     }
    			 }
    		 }
    		 
    		 DijkstraShortestPath<String, DefaultEdge> alg = new DijkstraShortestPath<String, DefaultEdge>(graph);
    		 
    		 Vector<String> topRed = new Vector<String>();
    		 Vector<Double> topRel = new Vector<Double>();
    		 
    		 for (int w=0; w<names.size(); w++) {
    			 if (topRel.size() == 0) {
    				 topRed.add(names.get(w));
    				 topRel.add(alg.getPathWeight(origin, names.get(w)));
    			 }
    			 else {
    				 int flag = 1;
    				 double weight = alg.getPathWeight(origin, names.get(w));
    				 if (weight != 0) {
	    				 for (int u=0; u<topRel.size(); u++) {
	    					 if (weight < topRel.get(u)) {
	    						 topRel.insertElementAt(weight, u);
	    						 topRed.insertElementAt(names.get(w), u);
	    						 flag = 0;
	    						 break;
	    					 }
	    				 }
	    				 if (flag == 1) {
	    					 topRel.add(weight);
	    					 topRed.add(names.get(w));
	    				 }
    				}
    			 }
    		 }
    		 if (limit > topRel.size()) for (int r=0; r<topRel.size(); r++) System.out.println(topRed.get(r) + "   " + topRel.get(r));
    		 else  for (int r=0; r<limit; r++) System.out.println(topRed.get(r) + "   " + topRel.get(r));
    		
    		
    	
    	} //closes else
    	
    }
}
