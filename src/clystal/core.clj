(ns clystal.core
  (:require [clojure.test :refer [function?]]))

(declare immediate-val? lookup-primitice-fun eval-list -eval)


(defn immediate-val? [exp]
  (number? exp))

(defn symbol-to-function [exp]
  (symbol (name exp)))

(defn lookup-var [exp env]
  (if (function? (symbol-to-function exp))
    (eval (symbol-to-function exp))
    (env exp)))

(defn eval-rest [exp env]
  (map -eval exp (repeat (count exp) env)))

(defn -eval [exp env]
  (if-not (vector? exp)
    (if (immediate-val? exp)
      exp
      (lookup-var exp env))
    (let
      [fun (-eval (first exp) env)
       args (eval-rest (rest exp) env)]
      (apply fun args))))
