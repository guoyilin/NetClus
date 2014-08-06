General description:
This is a bibliographic information network data set extracted from DBLP data, 
downloaded in the year 2008. The four_area data set includes papers from 4 areas, 
which are database, data mining, machine learning, and information retrieval. 
Each area contains five representative conferences. All the papers and authors 
in these 20 conferences are extracted.

File description:
conf.txt: dictionary for conferences. Format: ID \t Conference name
author.txt: dictionary for authors. Format: ID \t Author name
term.txt: dictionary for terms. Format: ID \t Term
paper.txt: dictionary for papers. Format: ID \t paper title

paper_conf.txt: relation file. Format: paperID \t confID
paper_author.txt: relation file. Format: paperID \t authorID
paper_term.txt: relation file. Format: paperID \t termID