(ns clms.parser
  (:require [clms.core :as core]
            [clojure.string :as str])
  (:use [clojure.tools.namespace.repl :only [refresh]]))

(defn filt [target]
  (-> target
      (str/replace #"\(" "[")
      (str/replace #"\)" "]")
      (str/replace #"\[" "[:")
      (str/replace #":\[" "[")
      (str/replace #" [A-Z]" #(str " :" (str/trim %)))
      (str/replace #" [a-z]" #(str " :" (str/trim %)))))

(defn parse [target]
  (read-string (filt target)))

(defn interpret [exp env]
  (core/-eval exp env))

(defn -main []
  (print "Clmsi > ")
  (flush)
  (println (interpret (parse (read-line)) [{}]))
  (-main))
