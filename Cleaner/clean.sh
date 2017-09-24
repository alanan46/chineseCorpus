#!/bin/bash
#delete all the empty data file
find . -name "url*.txt" -size -19c -delete		#for Miles,  comment it out
#put all smaller files into a big one
cat url*.txt > merged.txt				#for Miles,  comment it out
#get rid of all the tag
sed -i -e 's/<dialog>/ /g' -e 's/<\/dialog>/ /g'  -e 's/http:[[:alnum:]_-\.\/?&=]*//g' -e 's/https:[[:alnum:]_-\.\/?&=]*//g' merged.txt #for Miles,  comment it out
#append one in the beginning of the file
sed -i '1s/^/<dialog> /' merged.txt	#for Miles,  comment it out
#append to the end
echo '</dialog>' >>merged.txt		#for Miles,  comment it out