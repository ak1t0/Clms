(defproject clms "0.1.0-SNAPSHOT"
  :description "micro scheme in Clojure"
  :url "https://github.com/ak1t0/Clms"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.namespace "0.2.7"]]


  :main clms.parser
  :repl-options
  {:init-ns clms.parser
   :prompt (fn [ns] (str ns " > " ))})
