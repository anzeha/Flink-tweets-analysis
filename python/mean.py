import json

""" 
ELASTIC SEARCH COUNT DOCS AND AGGREAGTE REQUEST

POST http://localhost:9200/tweets_engcro/_search?size=0

body:
{
  "query": {
    "range": {
      "created_at": {
        "gte": "2021-06-13T12:30:00+00:00",
        "lte": "2021-06-13T15:00:00+00:00"
      }
    } 
  },
  "aggs": {
    "byday": {
      "date_histogram": {
        "field": "created_at",
        "interval": "minute"
      }
    }
  }
}



"""

def median(l):
    half = len(l) // 2
    l.sort()
    if not len(l) % 2:
        return (l[half - 1] + l[half]) / 2.0
    return l[half]

f = open("./aggregation.json",)

doc = json.load(f)

countAggs = doc["aggregations"]["byday"]["buckets"]

counts = []

for countAgg in countAggs:
    counts.append(countAgg["doc_count"])


print("median: ", median(counts))
print("avg:", sum(counts)/len(counts))
