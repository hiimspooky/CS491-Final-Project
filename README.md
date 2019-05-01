# CS491-Final-Project

Allows the user to input a URL and will try to recommend subreddits based on the link and user-selected options.

Implemented in Java, dependencies found in pom.xml

Brief summary of process:

1. Ask user for a URL
2. Search Reddit for which subreddits that link has been posted to
3. Search the top X posts of all time from each of those subreddits and find what subreddits those links have been posted to
4. Add a vertex for each subreddit from (2.) to a graph using JGraphT and draw an edge from the source vertex to each newly-added vertex*
5. For each subreddit found in (3.), add a vertex to the graph and draw an edge from the subreddit in which it was found to it*
6. Perform Djikstra's algorithm to find shortest path distances from the source vertex to every other vertex (subreddit) in the graph
7. Return the Y vertices (subreddits) found to be closest (Most relevant)

*Edge weights determined based on a user-selected option

Based on our findings, using the algorithms we implemented, post score seems to be the most optimal way to determine relevancy of a subreddit.
Determining relevancy based on post comment count provides interesting and valuable information, but is liable to being heavily influenced by odd occurrences.
