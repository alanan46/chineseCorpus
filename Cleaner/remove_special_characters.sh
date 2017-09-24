#just run
# example: s/XXX/YYY/g will replace all XXX to YYY
sed -i -e 's/http:[[:alnum:]_-\.\/?&=]*//g' merged.txt

#remove *
sed -i -e 's/&//g' merged.txt

#remove </utt>
sed -i -e 's/<\/utt>//g' merged.txt
# append </utt>
sed -i -e ' s/<utt[^<]*/&<\/utt>/g' merged.txt
#remove </s>
sed -i -e 's/<\/s>//g' merged.txt
# append </s> before <s>
sed -i -e ' s/<s>/<\/s>&/g' merged.txt
#append to the last </s>
sed -i -e ' s/<\/dialog>/<\/s>&/g' merged.txt
#remove the beginning
sed -i -e '0,/<\/s>/s/<\/s>//' merged.txt
