import numpy as np 
import pandas as pd 
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer, TfidfVectorizer  
import nltk 
nltk.download('stopwords')  
from nltk.corpus import stopwords

tweets = pd.read_csv("./tweets.csv")
tweetsInjury = pd.read_csv("./tweetsInjury.csv")
tweetsEngCroGoal = pd.read_csv("./tweets_engcro_goal.csv")
tweetsEngCroSubstitute = pd.read_csv("./tweets_engcro_substitute.csv")
tweetsEngCroStart = pd.read_csv("./tweets_engcro_start.csv")
tweetsEngCroeEnd = pd.read_csv("./tweets_engcro_end.csv")
tweetsSentimentTraining = pd.read_csv('./sentimenttraining2.csv', header=None)

print(tweetsSentimentTraining.count())


#print(tweetsInjury.iloc[1000:2000])
#CORPUS GOAL
#corpus = tweets.iloc[1:2000, 1].array
#CORPUS INJURJY
#corpus = tweetsInjury.iloc[1000:2000, 1].array
#CORPUS EURO ENGLAD CRATIA GOAL
#corpus = tweetsEngCroGoal.iloc[:, 1].array
#CORPUS EURO ENGLAND CRATIA SUBSTITUTE
#corpus = tweetsEngCroStart.iloc[:, 1].array
corpus = tweetsEngCroeEnd.iloc[:, 1].array

#tweetsEngCroeEnd.iloc[:,1].to_csv("./test.csv", index=False, line_terminator="\r\n", header=["text"])


tweetsSentimentTraining.iloc[:,1].to_csv("./sentimenttraining3.csv", index=False, line_terminator="\r\n", header=["text"])

fixedCorpus = []

for x in corpus:
    fixedCorpus.append(np.str_(x))



#instantiate CountVectorizer() 
cv=CountVectorizer(stop_words=stopwords.words('english'), max_features=200, max_df=0.7) 
 
# this steps generates word counts for the words in your docs 
word_count_vector=cv.fit_transform(fixedCorpus)


tfidf_transformer=TfidfTransformer(smooth_idf=True,use_idf=True) 
tfidf_transformer.fit(word_count_vector)

# print idf values 
df_idf = pd.DataFrame(tfidf_transformer.idf_, index=cv.get_feature_names(),columns=["idf_weights"]) 
 
# sort ascending 
weigths = df_idf.sort_values(by=['idf_weights'], ascending=True)
print(weigths) 
print(weigths.iloc[0:20]) 


""" tfidf_vectorizer = TfidfVectorizer(max_features=200, max_df=0.7, stop_words=stopwords.words('english')) 

tfidf_vectorizer_vectors=tfidf_vectorizer.fit_transform(goalTweets)
# get the first vector out (for the first document) 
print(tfidf_vectorizer.get_feature_names())
print(tfidf_vectorizer_vectors[0])

first_vector_tfidfvectorizer=tfidf_vectorizer_vectors[10] 
 
# place tf-idf values in a pandas data frame 
df = pd.DataFrame(first_vector_tfidfvectorizer.T.todense(), index=tfidf_vectorizer.get_feature_names(), columns=["tfidf"]) 
df.sort_values(by=["tfidf"],ascending=False)
print(df)
 """

""" df_countvect = pd.DataFrame(data = count_wm.toarray(),index = ['Doc1','Doc2'],columns = count_tokens)
df_tfidfvect = pd.DataFrame(data = tfidf_wm.toarray(),index = ['Doc1','Doc2'],columns = tfidf_tokens) """
""" print("Count Vectorizer\n")
print(df_countvect)
print("\nTD-IDF Vectorizer\n")
print(df_tfidfvect) """


""" X = tfidfconverter.fit_transform(goalTweets)
print(X)
#print(tfidfconverter.get_feature_names())
df = pd.DataFrame(X[0].T.todense(), index=tfidfconverter.get_feature_names(), columns=["TF-IDF"])
df = df.sort_values('TF-IDF', ascending=False)
print (df.head(25)) """