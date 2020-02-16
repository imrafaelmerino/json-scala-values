<a name="v3.0.0"></a>
## [v3.0.0](https://github.com/imrafaelmerino/json-scala-values/releases/tag/v3.0.0) (2020-02-08)

### BREAKING CHANGE
* (spec) implicit conversions are moved from value.Preamble to value.spec.Preamble

* (spec) enum renamed to conts 

* filter, filterKeys, map, mapKeys and reduce are renamed to filterAll, filterAllKeys, mapAll, mapAllKeys and reduceAll.

### Feat
* 🎸 JsObjFuture and JsArrayFuture 
* 🎸 JsObjTry and JsArrayTry
* 🎸 filter, map and reduce that doesn't traverse the json recursively (they only iterate through the first level)
* 🎸 concat function to merge Json object and arrays. Arrays can be treated as sets, multisets or lists

### Fix

### Docs
* ✏️ improved documentation 
* ✏️ added future and try in readme 
