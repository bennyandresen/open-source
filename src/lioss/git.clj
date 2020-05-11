(ns lioss.git
  (:require [clojure.java.shell :as sh]
            [clojure.string :as str]
            [lioss.util :as util]
            [clojure.java.io :as io]))

(defn git [& args]
  (str/trim (:out (apply sh/sh "git" args))))

(defn commit-count []
  (git "rev-list" "--count" "HEAD"))

(defn current-branch []
  (git "rev-parse" "--abbrev-ref" "HEAD"))

(defn current-sha []
  (git "rev-parse" "HEAD"))

(defn repo-clean? []
  (str/blank? (git "status" "--porcelain")))

(defn clean! []
  (git "clean" "-xfd"))

(defn version-string []
  (str
   (if (.exists (io/file ".VERSION_PREFIX"))
     (str/trim (slurp ".VERSION_PREFIX"))
     "0.0")
   "."
   (commit-count)))

(defn project-name []
  (let [url (git "remote" "get-url" "origin")]
    (second (re-find #"lambdaisland/([\.a-z0-9-]+)\.git" url))))

(defn assert-branch [branch]
  (let [b (current-branch)]
    (when-not (= b branch)
      (util/fatal "On branch" b ", must be on" branch))))

(defn assert-repo-clean []
  (when-not (repo-clean?)
    (util/fatal "Repo not clean." (git "status" "--short"))))
