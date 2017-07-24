#!/bin/bash
# Script to create most probable words from mallet.

echo "Mallet World!"

cd /home/jay/test
for f in *.txt
do 
  echo ${f%.txt}
  cp -v $f /malletInput/${f%.txt}.txt
  /home/jay/softwares/mallet/mallet-2.0.7/bin/mallet import-dir --input /malletInput/ --output ${f%.txt}.mallet --keep-sequence --remove-stopwords
  /home/jay/softwares/mallet/mallet-2.0.7/bin/mallet train-topics  --input ${f%.txt}.mallet --num-topics 20 --output-state topic-state.gz --output-topic-keys ${f%.txt}_tutorial_keys.txt --  output-doc-topics ${f%.txt}_tutorial_compostion.txt
  rm -f /malletInput/${f%.txt}.txt

done
