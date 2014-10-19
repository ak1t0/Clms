(ns clystal.core
  (:require [clojure.test :refer [function?]]))

(declare immediate-val? lookup-var eval-list -eval)
(declare lambda-to-exp unique-form let-to-lambda)
(declare lambda-to-closure closure-to-exp )
(declare eval-rest unique-form? key-to-function)

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


(defn unique-form [exp env]
  (let [head (first exp)]
    (cond
     (= head :let) (let-to-lambda exp env)
     (and (vector? head) (= (first head) :lambda)) (lambda-to-closure exp env)
     (= head :closure) (closure-to-exp exp env))))

(defn eval-rest [exp env]
  (map -eval exp (repeat (count exp) env)))


;;[:closure [:x :y] [:+ :x :y] {:x m}] [{:x a :y b}]]
;;
;;(-eval [:+ :x :y] [{:x m :y b}])

(defn closure-to-exp [closure env]
  (let [params (closure 1)
        body (closure 2)
        cls-env-map (closure 3)
        new-env-map (merge (first env) cls-env-map)]
    (-eval body (vector new-env-map))))


;;[[:lambda [:x :y] [:+ :x :y]] m n] [{:x a ;y b}]
;;
;;[:closure [:x :y] [:+ :x :y] {:x m :y n}]
;;
;;
;;[[:lambda [:x :y] [+ :x [[:lambda [:d :e] [:+ :d :e]] f g]]] m n]
;;
;;[:closure [:x :y] [:+ :x :y [[:lambda [:d :e] [:+ :d :e]] f g]] {:x m :y n}]

(defn lambda-to-closure [lambda env]
  (let [body (first lambda)
        args (rest lambda)
        params (second body)
        lambda-body (body 2)
        cls-env-map (apply hash-map (interleave params args))]
    (-eval (vector :closure params lambda-body cls-env-map) env)))


;;environment model [{ } { } ...]
;; empty env is [{}]



;;util function
;;

(defn lookup-var [exp env]
  (if (function? (key-to-function exp))
    (eval (key-to-function exp))
    (if (= ((first env) exp) nil)
      exp
      ((first env) exp))))

(defn immediate-val? [exp]
  (not (keyword? exp)))

(defn key-to-function [exp]
  (symbol (name exp)))

(defn unique-form? [exp]
  (let [head (first exp)]
    (or
     (and (vector? head) (= (first head) :lambda))
     (= head :closure)
     (= head :let))))
