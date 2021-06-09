import numpy as np 
import pandas as pd 
from sklearn.feature_extraction.text import CountVectorizer, TfidfTransformer, TfidfVectorizer  
import nltk 
nltk.download('stopwords')  
from nltk.corpus import stopwords

tweets = pd.read_csv("./tweets.csv")
print(tweets.shape)
corpus = tweets.iloc[25000:26000, 1].array


#instantiate CountVectorizer() 
cv=CountVectorizer(stop_words=stopwords.words('english'), max_features=200, max_df=0.7) 
 
# this steps generates word counts for the words in your docs 
word_count_vector=cv.fit_transform(corpus)

print(word_count_vector)

tfidf_transformer=TfidfTransformer(smooth_idf=True,use_idf=True) 
tfidf_transformer.fit(word_count_vector)

# print idf values 
df_idf = pd.DataFrame(tfidf_transformer.idf_, index=cv.get_feature_names(),columns=["idf_weights"]) 
 
# sort ascending 
print(df_idf.sort_values(by=['idf_weights'], ascending=True)) 


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