# Job+: A Java Based Job Recommendation Web Application
## About me
Hi! My name is Steve, and I'm a rising senior at [UWCSE](https://www.cs.washington.edu)! Feel free to connect me on [LinkedIn](https://www.linkedin.com/in/steve-ma/) or send me an [email](mailto:%20bochenma@cs.washington.edu)!
## About Job+
![Note: There was only one job listed on GitHub jobs in the State of Washington when I took the screenshot....](https://github.com/mastevb/JobRecommendation/blob/master/jobs_img/Screen%20Shot%202020-06-14%20at%204.27.02%20PM.png)
Job+ is a **prototyped** Java-based job search and recommendation web application that aims to improve job search and recommendation. The application recommends job postings based on users' geolocation and a content-based recommendation algorithm.
## GitHub Job PI
This project uses data fetched from GitHub jobs via the GitHub Job API. GitHub Job API is a web-based API provided by GitHub that allows clients to get retrieve job data from the GitHub Job server. Read more about their awesome API [here](https://jobs.github.com/api).
## Recommendation Algorithms
One of the most important features of this application is the ability to recommend job postings based on your previously liked postings. In this section, I will discuss several recommendation methods that I've learned while developing this project :)

**Content-based Recommendation**

The key concept behind a content-based recommendation is the idea that "people will like things of similar characteristics". In particular, it means that given different characteristics of an item (category, price, keyword) that a user has liked, recommend items that share the same profile. 

Content-based recommendations use the similarities within different items for a recommendation, and it is widely used for services under cold start conditions. For example, Apple Music lets you choose the kind of music you like during your first login, which is a content-based method for recommendations.

**Item-based Recommendation**

Item-based recommendation relies on the idea that an item should be recommended to a user who liked the same item as a user that liked that particular item. For example, item C is liked by users who liked item A, so recommend item C to users who like item A.

Step1: Calculate the similarities between two items based on information about the user groups that liked the items.

![enter image description here](https://github.com/mastevb/JobRecommendation/blob/master/jobs_img/Screen%20Shot%202020-06-20%20at%2012.04.51%20AM.png)

Note: M(i) stands for the union of users who liked item i.

Step2: Calculate the probability of user u liking item i based on the information of the kth most similar items to item i that is liked by user u, and the ratings for those items.

![enter image description here](https://github.com/mastevb/JobRecommendation/blob/master/jobs_img/Screen%20Shot%202020-06-20%20at%2012.06.54%20AM.png)

Note: 
Q(i, k) stands for the kth most similar item to i.
N(u) stands for the union of items liked by user u.
R_ui stands for the rating of item j given by user u.
A slight improvement that can be made to this method is considering the weight between inactive users and active users.

**User-based Recommendation**

A User-based Recommendation is almost the same as the Item-based recommendation, except that it focuses on the user group instead of the items. The idea behind the algorithm is that "user A shares similar preferences as user c, thus items liked by user C but not yet by user A should be recommended to user A.

Similarly, the calculation can be broken down into two steps, as the following.

![enter image description here](https://github.com/mastevb/JobRecommendation/blob/master/jobs_img/Screen%20Shot%202020-06-20%20at%2012.04.51%20AM.png)

Note: N(u) stands for the union of the items liked by user u.

![enter image description here](https://github.com/mastevb/JobRecommendation/blob/master/jobs_img/Screen%20Shot%202020-06-20%20at%2012.06.54%20AM.png)

Note: S(u, k) stands for the kth most similar users to user u

## Keyword Extraction
Since I've implemented a content-based recommendation strategy, an important aspect is retrieving keywords. Unfortunately, the GitHub Job API does not return pre-defined keywords, so we'll need to do it ourselves (not entirely). 

An important concept of keyword extraction is Term Frequency and Inverse Document Frequency. TF-IFG is one of the most common algorithms for keywords extraction that tells you the ordered keywords in a text document. Term Frequency stands for the frequency of a word in a document. However, in order to reduce common words across all sentences such as "a" or "the", we also need to use an Inverse Document Frequency score to filter out meaningless words.
The score for the keyword = TF(w) * IDF(w) = (number of w in document / number of all words in the document) * log (number of all documents / number of documents having w + 1).

I used the Monkey Learn API for this calculation. The [Monkey Learn API](https://monkeylearn.com/api/v3/) is one of the few keyword extraction APIs that is free and has detailed documentation and libraries. 
