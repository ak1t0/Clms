(ns clystal.core
  (:require [clojure.test :refer [function?]]))

(declare immediate-val? lookup-primitice-fun eval-list -eval)
(declare lambda-to-exp)


(defn immediate-val? [exp]
  (number? exp))

(defn unique-form? [exp]
  (let [head (first exp)]
    (if (and (vector? head) (= (first head) :lambda))
      true
      false)))


(defn key-to-function [exp]
  (symbol (name exp)))

(defn lookup-var [exp env]
  (if (function? (key-to-function exp))
    (eval (key-to-function exp))
    (env exp)))

(defn eval-rest [exp env]
  (map -eval exp (repeat (count exp) env)))

(defn -eval [exp env]
  (if-not (vector? exp)
    (if (immediate-val? exp)
      exp
      (lookup-var exp env))
    (if (unique-form? exp)
      (lambda-to-exp exp)
      (let
        [fun (-eval (first exp) env)
         args (eval-rest (rest exp) env)]
        (apply fun args)))))


;;(-eval [[:lambda [:x :y] [:+ :x :y]] n m])
;;
;;(-eval [:+ :x :y] {:x n :y m})

(defn lambda-to-exp [lambda]
  (let [exp (get (first lambda) 2)
        env (zipmap (second (first lambda)) (rest lambda))]
    (-eval exp env)))

