(ns postamuk.core
  (:require [cheshire.core   :as json]
            [postamuk.mudclient  :as http]))

(defn make-uuid [] (java.util.UUID/randomUUID))

(defn web-request [options on-complete]
  (try
    (let [
          start-time     (System/currentTimeMillis)
          ;response       (http/post options)
          response       (http/request options)
          elapsed-millis (- (System/currentTimeMillis) start-time)]
      (on-complete response))
    (catch Exception e
      (on-complete {:error e})  )))

(defn- http-response-callback
  "this is used in a partial context where the first four args are given in the  post-webhook function
   and the call is completed with the last arg (the response) from the web-request function
  "
  [params webhook-id start-time span-info response]
  (let [elapsed-millis (- (System/currentTimeMillis) start-time)
        status         (:status response)
        error          (:error  response) ]
    (println (str status " error: " error)) ) )

(defn post-webhook
  "Posts the body to the http url...~ pigeon/src/pigeon/workers/webhooks.clj (defn process-webhook...)"
  [url body webhook-id]
  (let [{:keys [message rule]} body
        request    {;:async?           true
                    :url              url
                    :request-method           :post
                    :body             (json/generate-string body)
                    :conn-timeout     1000
                    :socket-timeout   5000
                    ;:throw-exceptions false
                    :throw-exceptions true
                    :debug            true
                    :debug-body       true
                    :label            "webhook"}
        span-info  {:requestSpan {
                                  ;  :timestamp          (timestamp)
                                  :serviceName        "webhook"
                                  :stack              "postamuk"
                                  :unhandledException nil
                                  :handledExceptions  nil}
                    :webhookInfo (-> rule
                                     (select-keys [:AppName :AppUrl :OwnerID :ObjectUUID :SubscriptionID :TargetUrl])
                                     (update :SubscriptionID str)
                                     (merge {:changeUserId  "770099876" :changeUserName "dorwinkly"}))}
        start-time (System/currentTimeMillis)]
    (try
      (web-request request
                   (partial http-response-callback
                            {:url      (:url request)}
                            webhook-id
                            start-time
                            span-info))
      (catch Exception e
        (println (str e "there was a great disturbance in the septic tank"))
        ;(log/error {:exception  e
        ;            :message    "postamuk-webhook-processing-error"
        ;            :webhook-id webhook-id
        ;            :url        (:url request)
        ;            :span-info  span-info
        ;            :stack      "postamuk"})
        (throw e)
        )
      )
    )
  )


(def webhook-payload
  {:message {
             "message_id" "a9f29192-ad9e-4cf9-bd72-a54088659ee1"
             "message_version" 2
             "id" "f25043d0-ee75-4a53-834b-6988fdc2d153"
             "subscription_id" 209
             "project" { "name" "Static" "uuid" "7e62d4bd-d6a1-4d69-8e76-9a84ccc1f75f" }
             "detail_link" "https://rally1.rallydev.com/slm/#/detail/defect/91975509676"
             "object_id" "f25043d0-ee75-4a53-834b-6988fdc2d153"
             "object_type" "Defect"
             "ref" "https://rally1.rallydev.com/slm/webservice/v2.x/defect/f25043d0-ee75-4a53-834b-6988fdc2d153"
             "transaction" {
                            "trace_id" "46ddd8c9-9968-4f04-9426-058795d117b1"
                            "timestamp" 1540764556960
                            "user" {
                                    "uuid" "4c3f7050-3d68-4f5d-8b6b-e9ef0b21d69a"
                                    "username" "yeti@rallydev.com"
                                    "email" "yeti@rallydev.com"
                                    }
                            "message_id" "a9f29192-ad9e-4cf9-bd72-a54088659ee1"
                            "parent_span_id" "9e1dc398-ce84-42ff-8383-dca2b212026d"
                            "span_id" "a5e13111-9090-4856-a183-2ae2b2e77e08"
                            "message_lag" 32
                            }
             :state    {
                        "c3d4f057-0781-4660-8ce7-bc8514bf0945" {
                                                                "display_name" "ObjectUUID"
                                                                "name" "ObjectUUID"
                                                                "type" "Raw"
                                                                "value" "f25043d0-ee75-4a53-834b-6988fdc2d153"
                                                                }
                        "500a0d67-9c48-4145-920c-821033e4a832" {
                                                                "display_name" "Name"
                                                                "name" "Name"
                                                                "type" "String"
                                                                "value" "wombats are us WRU"
                                                                }
                        "55c5512a-1518-4944-8597-3eb91875e8d1" {
                                                                "display_name" "Formatted ID"
                                                                "name" "FormattedID"
                                                                "type" "String"
                                                                "value" "DE8"
                                                                }
                        "fd9e708c-402d-c1c8-9a4c-6c7802432ae5" {
                                                                "display_name" "Formatted ID Prefix"
                                                                "name" "FormattedIDPrefix"
                                                                "type" "String"
                                                                "value" "DE"
                                                                }
                        "95ac2b13-e4ee-413e-af83-42aaf4b53626" {
                                                                "display_name" "Creation Date"
                                                                "name" "CreationDate"
                                                                "type" "Date"
                                                                "value" "2017-02-08T22:39:19.957Z"
                                                                }
                        "557393c4-131b-4df7-b01e-5319b11edd24" {
                                                                "display_name" "Owner"
                                                                "name" "Owner"
                                                                "type" "User"
                                                                "value" nil
                                                                }
                        "d3e4464d-2aa0-4727-903a-57a2af5316ba" {
                                                                "display_name" "Priority"
                                                                "name" "Priority"
                                                                "type" "Rating"
                                                                "value" { "value" "High Attention", "ordinal_index" 2 }
                                                                }
                        "f5b1fb22-6c15-44b5-a592-19189fafe5f2" {
                                                                "display_name" "VersionId"
                                                                "name" "VersionId"
                                                                "type" "Integer"
                                                                "value" 39
                                                                }
                        "727a8977-f3a5-4713-9415-ac97471cc736" {
                                                                "display_name" "Ready"
                                                                "name" "Ready"
                                                                "type" "Boolean"
                                                                "value" false
                                                                }
                        "ae8ecc9f-b9a0-42a4-a6e3-c83d7f8a7070" {
                                                                "display_name" "Project"
                                                                "name" "Project"
                                                                "type" "Project"
                                                                "value" {
                                                                         "name" "Static"
                                                                         "ref" "https://rally1.rallydev.com/slm/webservice/v2.x/project/7e62d4bd-d6a1-4d69-8e76-9a84ccc1f75f"
                                                                         "detail_link" "https://rally1.rallydev.com/slm/#/detail/project/69501631984"
                                                                         "id" "7e62d4bd-d6a1-4d69-8e76-9a84ccc1f75f"
                                                                         "object_type" "Project"
                                                                         }
                                                                }
                        "7f25a8ae-6948-49a9-92f5-b437ca213251" {
                                                                "display_name" "Plan Estimate"
                                                                "name" "PlanEstimate"
                                                                "type" "Quantity"
                                                                "value" { "value" 3 "units" "Points" }
                                                                }
                        }
             :changes  {
                        "f5b1fb22-6c15-44b5-a592-19189fafe5f2" {
                                                                "display_name" "VersionId"
                                                                "name" "VersionId"
                                                                "type" "Integer"
                                                                "old_value" 38
                                                                "value" 39
                                                                }
                        "e0caf6dd-304b-447e-9d61-09ac9c96e85a" {
                                                                "display_name" "State"
                                                                "name" "State"
                                                                "type" "Rating"
                                                                "old_value" { "value" "Fixed" "ordinal_index" 3 }
                                                                "value"     { "value" "Closed" "ordinal_index" 4 }
                                                                }
                        "24a49498-e2d6-4ded-b200-313933da132e" {
                                                                "display_name" "Closed Date"
                                                                "name" "ClosedDate"
                                                                "type" "Date"
                                                                "old_value" nil
                                                                "value" "2018-10-28T22:09:16.922Z"
                                                                }
                        "b8deb4b7-8b43-442f-8026-6c0dfa795093" {
                                                                "display_name" "Opened Date"
                                                                "name" "OpenedDate"
                                                                "type" "Date"
                                                                "value" "2018-09-12T20:20:02.964Z"
                                                                }
                        "b5bae981-bfe6-4980-844a-b5ecf3e9a156" {
                                                                "display_name" "Description"
                                                                "name" "Description"
                                                                "type" "Text"
                                                                "value" "asfasfasfasfasf- wombats are us"
                                                                }
                        "2910ca71-9779-4e7b-8fcf-d718c919aff8" {
                                                                "display_name" "Object ID"
                                                                "name" "ObjectID"
                                                                "type" "Integer"
                                                                "value" 91975509676
                                                                }
                        "244502f5-6bad-4a39-88d4-86446f43c399" {
                                                                "display_name" "Severity"
                                                                "name" "Severity"
                                                                "type" "Rating"
                                                                "value" { "value" "None" "ordinal_index" 0 }
                                                                }
                        }
             :action  "Updated"
             }
   :rule     {:_type  "webhook"
              :_objectVersion  2
              :ObjectUUID  "2b8b4022-1385-44f9-bc1d-609f59d333e3"
              :CreationDate  "2018-09-13T22:26:53.684Z"
              :OwnerID  "4c3f7050-3d68-4f5d-8b6b-e9ef0b21d69a"
              :LastUpdateDate  "2018-09-13T22:26:53.847Z"
              :SubscriptionID  209
              :Name  "completed stories and defects"
              :TargetUrl  "http://alligator.proxy.beeceptor.com"
              :AppName  "jakaloof-foo"
              :AppUrl  "foobar.com"
              :ObjectTypes  [ "Defect", "HierarchicalRequirement" ]
              :Expressions  [
                             { "AttributeName" "ScheduleState" "Operator" "=" "Value" "Completed" }
                             { "AttributeName" "Workspace"     "Operator" "=" "Value" "7d1bc994-cb0a-4d2d-b172-62f81912ad34" }
                             ]
              :Disabled  false
              }
   }
  )


(defn -main[]
  (println "got here")
  (let [target_url  "http://localhost:3000/event"
        payload     webhook-payload
        outcome    (post-webhook target_url payload (make-uuid))]
    (println outcome)))
