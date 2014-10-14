(ns clystal.core)

(declare immediate-val? lookup-primitice-fun eval-list -eval)


(defn immediate-val? [exp]
  (number? exp))

(defn lookup-primitive-fun [exp]
  (eval (symbol (name exp))))

(defn eval-list [exp]
  (map -eval exp))

(defn -eval [exp]
  (if-not (vector? exp)
    (if (immediate-val? exp)
      exp
      (lookup-primitive-fun exp))
    (let
      [fun (-eval (first exp))
       args (eval-list (rest exp))]
      (apply fun args))))
