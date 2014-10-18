(ns clystal.core
  (:require [clojure.test :refer [function?]]))

(declare immediate-val? lookup-var eval-list -eval)
(declare lambda-to-exp unique-form let-to-lambda)


(defn immediate-val? [exp]
  (number? exp))

(defn unique-form? [exp]
  (let [head (first exp)]
    (if (or
         (and (vector? head) (= (first head) :lambda))
         (= (first exp) :let))
      true
      false)))

(defn unique-form [exp env]
  (cond
   (= (first exp) :let) (let-to-lambda exp env)
   (= (first (first exp)) :lambda) (lambda-to-exp exp env)))


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


;;(-eval [[:lambda [:x :y] [:+ :x :y]] n m])
;;
;;(-eval [:+ :x :y] {:x n :y m})

(defn lambda-to-exp [lambda env]
  (let [exp (get (first lambda) 2)
        local-env (zipmap (second (first lambda)) (rest lambda))]
    ;exp
    ;(apply (partial vector local-env) env)
    (-eval exp (apply (partial vector local-env) env))
    ))


;;[:let [[:x m] [:y n]] [:+ :x :y]]
;;
;;[[:lambda [:x :y] [:+ :x :y] m n]
(defn let-to-lambda [let-ex env]
  (let [params (get let-ex 1)
        body (get let-ex 2)
        local-env (apply hash-map (interleave (map first params) (map second params)))]
   (-eval
    (into
     (vector
      (vector :lambda (vec (map first params)) body))
     (map second params))
     local-env
     )
  ))


;;[:lambda [:x :y] [:+ :x :y]] env-vec
;;
;;[:closure [:x :y] [:+ :x :y] env-map]

(defn lambda-to-closure [exp env]
  (vector :closure (exp 1) (exp 2) (first env)))

;;[:closure [:x :y] [:+ :x :y] {:x m}] [{:x a :y b}]
;;
;;(-eval [:+ :x :y] [{:x m :y b}])

(defn closure-to-exp [closure args]
  (let [params (closure 1)
        body (closure 2)
        cls-env-map (closure 3)
        arg-env-map (apply hash-map (interleave params args))
        new-env-map (merge arg-env-map cls-env-map)]
    ;(list body new-env)
    (-eval body (vector new-env-map))))


;;environment model [{ } { } ...]

(defn lookup-var [exp env]
  (if (function? (key-to-function exp))
    (eval (key-to-function exp))
    ((first env) exp)))
