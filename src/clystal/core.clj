(ns clystal.core
  (:require [clojure.test :refer [function?]]))

(declare immediate-val? lookup-var eval-list -eval)
(declare lambda-to-exp unique-form let-to-lambda)
(declare lambda-to-closure closure-to-exp -apply -eval2)


(defn immediate-val? [exp]
  (number? exp))

(defn unique-form? [exp]
  (let [head (first exp)]
    (or
     (= head :lambda)
     (= head :closure)
     (= head :let))))

(defn unique-form [exp env]
  (cond
   (= (first exp) :let) (let-to-lambda exp env)
   (= (first exp) :lambda) (lambda-to-closure exp env)
   (= (first exp) :closure) (closure-to-exp exp env)
   ))


(defn key-to-function [exp]
  (symbol (name exp)))

(defn eval-rest [exp env]
  (map -eval exp (repeat (count exp) env)))

(defn -eval [exp env]
  (if-not (vector? exp)
    (if (immediate-val? exp)
      exp
      (lookup-var exp env))
    (if (unique-form? exp)
      (unique-form exp env)
        (let
          [fun (-eval (first exp) env)
           args (eval-rest (rest exp) env)]
          (apply fun args)))))


;;[:closure [:x :y] [:+ :x :y] {:x m}] [{:x a :y b}]]
;;
;;(-eval [:+ :x :y] [{:x m :y b}])

(defn closure-to-exp [closure env]
  (let [params (closure 1)
        body (closure 2)
        cls-env-map (closure 3)
        new-env-map (merge (first env) cls-env-map)]
    (-eval body (vector new-env-map))))


;;environment model [{ } { } ...]

(defn lookup-var [exp env]
  (if (function? (key-to-function exp))
    (eval (key-to-function exp))
    (if (= ((first env) exp) nil)
      exp
      ((first env) exp))))
