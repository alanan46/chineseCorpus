#Dependency

The parser depends on jsoup-1.10.3.jar.
The R code needs library ggplot2


# Steps

## Crawler

All the URLs related to economics or economy were crawled via python script using HTML and CSS selectors.
Input: Primary_URL to be placed in self.crawl method 
Output: List of URLs that acts as an input to the parser


## Parser

Set the filename in line 31 of parser/main.java to be the name of the url file. The name of the url file should starts with "url". Each line of that file should be a url.

## Cleaner

Copy the output files of the parser to cleaner folder. Run parser/main.java to get the final corpus (final_corpus.txt) and the statistical data (final_stats.txt and pp_stats.txt). Meanwhile, you can get remove_map.txt which records how many utterances are filtered because of a specific keywords and delete.log which records each deleted utterance are deleted because of which keyword.

## Statistical Graph Generator
chmod +x stats.R && ./stats.R
