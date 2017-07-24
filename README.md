# YelpDatasetChallenge
<div><b>Tastk 1</b> - Predict category of a business from its reviews and tips - Tools Used - Lucene API, Mallet API </div>
<br>
<div><b>Methodology:</b></div>

  <ul>
    <li>We choose the information retrieval approach to predict the categories from user’s reviews and tips for businesses. We made index using the Lucene and partitioned the dataset as:</li>
	  <li>60% for the training .data set with 783 documents as categories, including reviews and tips as fields for the documents</li>
	  <li>The remaining 40% is used as test dataset. We provided POS tagging for the whole test data and used it as a query expansion for the better efficiency.</li>
    <li>The results for normal query and POS tagged query are compared and performance is measured.</li>
  </ul>

<br>

<div><b>Task 2 </b>-  The proposed question for task 2 is intended to help local businesses leverage Yelp data by identifying specific local influencers</div>
<br>
<div><b>Methodology: </b></div>
<ul>
  <p>For identifying influencers pertinent to a specific business category, there are three steps at a higher level:</p>
  <li><b>Identifying User Locality:</b></li>
    <ol>
      <li>Since Yelp dataset does not explicitly mention the locality of a user, we will have to glean this indirectly. We propose to use MongoDB for this task for running search on the corpus. Following are the steps involved:</li>
      <li>	Identifying all locations a user has reviewed by using the user collection and the business collection.</li>
      <li>	Learning the user location by using Gaussian mixture model</li>
    </ol>
  <br>
  <li><b>Identifying Social Influence:</b></li>
    <p>Analyzing the following features to come up with a weighted score for each user and sorting this score to identify influence:</p>
    <ol>
      <li>Friend list, number of fans and number of votes on the reviews, analyzing the feedback on his reviews.</li>
      <li>Identifying Influencer Expertise:</li>
   	  <li>User expertise will be gleaned by understanding the number of reviews written by a user for different categories, the amount of engagement on these reviews for each category (engagement is defined as the number of votes and feedback on the reviews), length of reviews, average rating and variance with actual rating, period of time yelping for, etc.</li>
    </ol>
<ul>

<div> 
    List of Files Used:
</div>

<ol>
    <li><b>Category.java :</b> This file is used to make a Category Map - childCategory gets mapped to its ParentCategory</li>
    <li><b>Evaluation.java :</b> This file is used to calculate precision and recall</li>
    <li><b>LuceneIndexWriter.java :</b> This is a generic class to create index</li>
    <li><b>POSIndex.java :</b> This class creates a POSMalletIndex in Lucene</li>
    <li><b>POSTagger.java :</b> This is a generic class which returns a POS tagged string when given a normal string</li>
    <li><b>Search.java :</b> This is a generic class to search in index.</li>
    <li><b>MainClass.java :</b> This is the driver class</li>
    
</ol>
