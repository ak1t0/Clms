# Clms

Clms is instant micro scheme interpreter *in* Clojure.

Clms expression is Clojure vector and environment is Clojure map vector.

## Usage

Run Clms

    $ lein repl

eval Clms expression

```clojure

Clms > (-eval [:+ :x :y] [{:x 5 :y 7}])
=> 12

Clms > (-eval [[:lambda [:x  :y] [:+ :x :y]] 3 2] [{:x 5 :y 7}])
=> 5

Clms > (-eval [[:lambda [:x] [:+ [[:lambda [:y] :y] 2] :x]] 1] [{:x 5 :y 7}])
=> 3

Clms > (-eval [:let [[:x 3] [:y 2]] [:+ :x :y]] [{:x 5 :y 7}])
=> 5

Clms > (-eval [:if [:> 3 2] 1 0] [{}])
=> 1

Clms > (-eval [:let
               [[:fact
                 [:lambda [:n] [:if [:< :n 1] 1 [:* :n [:fact [:- :n 1]]]]]]]
               [:fact 5]] [{}])
=> 120

```

##Reference document
* [「つくって学ぶプログラミング言語 RubyによるScheme処理系の実装」](http://tatsu-zine.com/books/scheme-in-ruby)

## License

Copyright © 2014 ak1t0

Distributed under the Eclipse Public License.
