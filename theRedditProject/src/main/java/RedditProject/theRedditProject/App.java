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
    		for (int a = 0; a < splitText.length; a++) {
    			if (splitText[a].contains("\"subreddit\":")) {
    				subreddits_string.add(splitText[a+1]);
    			}
    			else if (splitText[a].contains("\"subreddit_subscribers\":")) subreddits_subs.add(splitText[a+1]);
    			else if (splitText[a].contains("\"score\":")) subreddits_scores.add(splitText[a+1]);
    		}
    		
    		for (int c = 0; c < subreddits_subs.size(); c++) {
    			subreddits_subs.set(c, subreddits_subs.get(c).replace(",", ""));
    		}
    		
    		for (int c = 0; c < subreddits_scores.size(); c++) { 
    			subreddits_scores.set(c, subreddits_scores.get(c).replace(",", ""));
    		}
    		
    		for (int c = 0; c < subreddits_string.size(); c++) {
    			String temp = subreddits_string.get(c);
    			temp = temp.replaceFirst("\"", "");
    			temp = temp.replace("\",", "");
    			subreddits_string.set(c, temp);
    		}
    		
    		for (int x = 0; x < subreddits_string.size(); x++) System.out.println(subreddits_string.get(x) + ": " + subreddits_subs.get(x) + " (" + subreddits_scores.get(x) + ")");
    		
    		System.out.println("Enter a search degree for related subreddits (Suggested 0,5,10):");
    		
    		searchDegree = Integer.parseInt(myObj.nextLine());
    		
    		Vector<Double> subreddits_relevancy = new Vector<Double>();
    		
    		for (int x = 0; x < subreddits_string.size(); x++) subreddits_relevancy.add(Double.parseDouble(subreddits_scores.get(x))/Double.parseDouble(subreddits_subs.get(x)));
    		
    		for (int x = 0; x < subreddits_relevancy.size(); x++) System.out.println(subreddits_relevancy.get(x));
    		
    		

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
    			for (int y = 0; y < DEFAULT_DEPTH; y++) {
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
    		
    		System.out.println(total_subreddit_list.size());
    		for (int x = 0; x < total_subreddit_list.size(); x++) {
    			System.out.println(total_subreddit_list.get(x).name + " with " + total_subreddit_list.get(x).subscribers + " subscribers");
    			for (int y = 0; y < total_subreddit_list.get(x).urls.size(); y++) {
    				System.out.println(total_subreddit_list.get(x).urls.get(y));
    				System.out.println(total_subreddit_list.get(x).numComments.get(y));
    			}
    		}
    		
    		
    		
    		/*
    		for (Submission post : firstPage) {
    			System.out.println(String.format("%s (/r/%s, %s points) - %s",
                        post.getTitle(), post.getSubreddit(), post.getScore(), post.getUrl()));
    		}
    		*/
    		
    		
    		
    		
    		
    		//Construct graph
    		
    		
    		
    		
    		
    		
    		
    		
    	
    	} //closes else
    	
    }
}