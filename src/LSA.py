# This file is uses LSA algorithm for query algorithm
import os
import sys
import json
from urllib2 import Request, urlopen, URLError


path = os.path.realpath(__file__)
path = path[:16]
o_path = path + '/LSA_Output'
path = path+'lsa/LSA_Input'
# file_path = 'LSA'

data = {}
for dir_entry in os.listdir(path):
    file_path = os.path.join(path, dir_entry)
    wrte_path = os.path.join(o_path, dir_entry)
    if os.path.isfile(file_path):
        print 'working on file', file_path
        with open(file_path, 'r') as inpt_file:
            otpt_fle = open('otpt.txt', 'w')
            for lines in inpt_file:
                inpt_lst = []
                rslt_dct = {}
                lines = lines.split()
                # count = lines[0]
                for key in lines:
                    if key.isalpha():
                        # print key
                        rslt_lst = []
                        inpt_lst.append(key)
                        api_call = 'https://api.datamuse.com/words?rd=' + key
                        request = Request(api_call)
                        try:
                            response = urlopen(request)
                            rel_wrds = response.read()
                            # print rel_wrds, '\n'
                            rel_wrds = rel_wrds.replace('[', '')
                            rel_wrds = rel_wrds.replace(']', '')
                            rel_wrds = rel_wrds.replace('{', '')
                            rel_wrds = rel_wrds.replace('}', '')
                            rel_wrds = rel_wrds.replace('"', '')
                            rel_wrds = rel_wrds.replace(':', ' ')
                            rel_wrds = rel_wrds.replace(',', ' ')
                            rel_wrds = rel_wrds.replace('score', '')
                            rel_wrds = rel_wrds.replace('word', '')
                            rel_wrds = rel_wrds.split(' ')
                            rel_wrds = filter(None, rel_wrds)
                            print '\n', rel_wrds
                            rel_wrds = rel_wrds[:10]
                            print '\n', rel_wrds
                            for word in rel_wrds:
                                if (word != 'score') and (word != 'word') and (word.isdigit() == False) and (word != ''):
                                    # print count, word
                                    rslt_lst.append(word)
                            print '\n',key
                            rslt_dct[key] = rslt_lst

                        except URLError, e: print "Error"
                    else: pass
                        # print "\n\n", count, '\n', inpt_lst
                        # json.dump(empt_dct,otpt_fle)
                        # json.dump(string)
                    

                
                # json.dump(rslt_dct, otpt_fle)
                for key in rslt_dct.keys():
                    otpt_fle.write('\n\n')
                    otpt_fle.write(key + ' : ')
                    for itms in rslt_dct[key]:
                        otpt_fle.write(itms + ' ')


otpt_fle.close()
