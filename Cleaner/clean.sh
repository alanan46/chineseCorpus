#!/bin/bash
#delete all the empty data file
find . -name "saved/final_url*.txt" -size -19c -delete
#put all smaller files into a big one
cat saved/final_url*.txt > merged.txt
#get rid of all the tag
sed -i -e 's/<dialog>/ /g' -e 's/<\/dialog>/ /g' merged.txt
#append one in the beginning of the file
sed -i '1s/^/<dialog> /' merged.txt
#append to the end
echo '</dialog>' >>merged.txt
