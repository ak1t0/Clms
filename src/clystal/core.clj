(ns clystal.core
  (:require [clojure.test :refer [function?]]))

(declare immediate-val? lookup-primitice-fun eval-list -eval)


(defn immediate-val? [exp]
  (number? exp))

(defn symbol-to-function [exp]
  (eval (symbol (name exp))))

(defn lookup-var [exp]
  (if (function? (symbol-to-function exp))
    (eval (symbol-to-function exp))
    nil))

(defn eval-rest [exp]
  (map -eval exp))

(defn -eval [exp]
  (if-not (vector? exp)
    (if (immediate-val? exp)
      exp
      (lookup-var exp))
    (let
      [fun (-eval (first exp))
       args (eval-rest (rest exp))]
      (apply fun args))))
