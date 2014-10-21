(ns clms.core
  (:require [clojure.test :refer [function?]])
  (:use [clojure.tools.namespace.repl :only [refresh]]))

(declare immediate-val? lookup-var eval-list -eval)
(declare lambda-to-exp unique-form let-to-lambda)
(declare lambda-to-closure closure-to-exp)
(declare eval-rest unique-form? key-to-function)
(declare if-to-exp -apply)

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
          (-apply fun args env)))))

(defn -apply [fun args env]
  (if (function? fun)
    (apply fun args)
    (-eval (apply vector fun args) env)))


(defn unique-form [exp env]
  (let [head (first exp)]
    (cond
     (= head :let) (let-to-lambda exp env)
     (and (vector? head) (= (first head) :lambda)) (lambda-to-closure exp env)
     (= head :closure) (closure-to-exp exp env)
     (= head :if) (if-to-exp exp env))))

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
    ;(vector :closure params lambda-body cls-env-map)
    (-eval (vector :closure params lambda-body cls-env-map) env)
    ))


;;[:let [[:x 3] [:y 4]] [+ :x :y]]
;;
;;[[:lambda [:x :y] [:+ :x :y]] 3 4]
;;
;;
;;[:let [[:x m] [:y n]] [:let [[:x a] [:y b]] [:* :x :y]]]
;;
;;[:lambda [:x :y] [:let [[:x a] [:y b]] [* :x :y]] m n]

(defn let-to-lambda [exp env]
  (let [body (exp 2)
        params (map first (exp 1))
        args (map second (exp 1))]
    ;(apply vector (vector :lambda (vec params) (vec body)) args)
    (-eval (apply vector (vector :lambda (vec params) (vec body)) args) env)
    ))

;;[:if [:< m n] a b] env
;;
;;(-eval a env)

(defn if-to-exp [exp env]
  (let [cond-exp (exp 1)
        true-exp (exp 2)
        false-exp (exp 3)]
  (if (-eval cond-exp env)
    (-eval true-exp env)
    (-eval false-exp env))))


;;environment model [{ } { } ...]
;; empty env is [{}]



;;util function
;;


;;lookup var
;;
;; :function to function
;; :boolean to :boolean
;; :variable to value in env
;; :symbol to symbol

(defn lookup-var [exp env]
  (if (function? (key-to-function exp))
    (eval (key-to-function exp))
    (if (or (= exp :false) (= exp :true))
      (cond
       (= exp :false) false
       (= exp :true) true)
      (if (= ((first env) exp) nil)
        exp
        ((first env) exp)))))

(defn immediate-val? [exp]
  (not (keyword? exp)))

(defn key-to-function [exp]
  (symbol (name exp)))

(defn unique-form? [exp]
  (let [head (first exp)]
    (or
     (and (vector? head) (= (first head) :lambda))
     (= head :closure)
     (= head :let)
     (= head :if))))
