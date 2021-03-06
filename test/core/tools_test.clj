(ns core.relational-test)

(deftest relfn-test
  (is (= '(= 20 (:status t))
         (:body (meta (relfn [t] (= 20 (:status t))))))))

(deftest same-type?-test
  (testing "Comparing types of two relations"
    (let [rel1 (rel [:id :name] #{})
          rel2 (rel [:name :id] #{})
          rel3 (rel [:key :name] #{})] 
      (is (true? (same-type? rel1 rel2)))
      (is (false? (same-type? rel1 rel3)))
      (is (false? (same-type? rel2 rel3))))))

(deftest attr-exist?-test
  (let [empty-rel (rel [] #{})
          rel (rel [:id :name :phone] #{})]
    (testing "Checking for existence of a single attribute"
      (is (true? (attr-exist? rel :name)))
      (is (false? (attr-exist? rel :street)))
      (is (false? (attr-exist? empty-rel :name))))
    
    (testing "Checking for existence of several attributes"
      (is (true? (attr-exist? rel :name :phone)))
      (is (true? (attr-exist? rel :phone :name)))
      (is (true? (attr-exist? rel :name :name)))
      (is (false? (attr-exist? rel :name :street)))
      (is (false? (attr-exist? empty-rel :name :id))))))

(deftest attr-not-exist?-test
  (let [empty-rel (rel [] #{})
          rel (rel [:id :name :phone] #{})]
    (testing "Checking for existence of a single attribute"
      (is (false? (attr-not-exist? rel :name)))
      (is (true? (attr-not-exist? rel :street)))
      (is (true? (attr-not-exist? empty-rel :name))))
    
    (testing "Checking for existence of several attributes"
      (is (false? (attr-not-exist? rel :name :phone)))
      (is (false? (attr-not-exist? rel :phone :name)))
      (is (false? (attr-not-exist? rel :name :name)))
      (is (true? (attr-not-exist? rel :name :street)))
      (is (true? (attr-not-exist? empty-rel :name :id))))))

(deftest index-of-test
  (let [my-coll [:id :name :phone]]
    (is (= 0 (index-of my-coll :id)))
    (is (= 1 (index-of my-coll :name)))
    (is (= 2 (index-of my-coll :phone)))
    (is (nil? (index-of my-coll :street)))))

(deftest same-attr-order?-test
  (let [rel1 (rel [:id :name] #{})
        rel2 (rel [:name :id] #{})]
    (is (false? (same-attr-order? rel1 rel2)))
    (is (true? (same-attr-order? rel1 (rel [:id :name] #{}))))
    (is (true? (same-attr-order? 
                 (rel [] #{})
                 (rel [] #{}))))))

(deftest sort-vec-test
  (let [rel1 (rel [:id :name :phone] #{})
        rel2 (rel [:name :phone :id] #{})
        rel3 (rel [:name :id] #{})
        empty-rel (rel [] #{})]
    (is (= [2 0 1] (sort-vec rel1 rel2)))
    (is (= [0 1 2] (sort-vec rel1 rel1)))
    (is (= [0 1 2] (sort-vec rel2 rel2)))
    (is (= [] (sort-vec empty-rel empty-rel)))
    (is (thrown? IllegalArgumentException (sort-vec rel1 empty-rel)))
    (is (thrown? IllegalArgumentException (sort-vec empty-rel rel2)))
    (is (thrown? IllegalArgumentException (sort-vec rel2 rel3)))
    (is (thrown? IllegalArgumentException (sort-vec rel3 rel1)))))

(deftest common-attr-test
  (let [rel1 (rel [:id :name] #{})
        empty-rel (rel [] #{})]
    (is (= [:id] (common-attr rel1 (rel [:id :phone] #{}))))
    (is (= [] (common-attr rel1 (rel [:key :phone] #{}))))
    (is (= [:id :name] (common-attr rel1 rel1)))
    (is (= [] (common-attr empty-rel empty-rel)))
    (is (= [] (common-attr empty-rel rel1)))))

(deftest diverging-attr-test
  (let [rel1 (rel [:id :name] #{})
        empty-rel (rel [] #{})]
    (is (= [:id] (diverging-attr rel1 (rel [:name :phone] #{}))))
    (is (= [] (diverging-attr rel1 rel1)))
    (is (= [:id :name] (diverging-attr rel1 empty-rel)))
    (is (= [] (diverging-attr empty-rel rel1)))
    (is (= [:name] (diverging-attr rel1 (rel [:id :phone] #{}))))))

(deftest attr-complement-test
  (let [attrs [:id :name :phone :address]
        r (rel attrs #{})]
    (is (= attrs (attr-complement r nil)))
    (is (= attrs (attr-complement r [])))
    (is (= [:id :name :address] (attr-complement r :phone)))
    (is (= [:id :address] (attr-complement r [:name :street :phone])))))