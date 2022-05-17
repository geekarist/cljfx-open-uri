## How to reload an effect function in a running app?

### Problem

I'm using `create-app` to implement a simple application with side effects, but when I change an effect function, redefine it into the REPL, then re-render the app, it doesn't "see" the change.

To illustrate this behaviour I have written a simple example:
- See this repo: https://github.com/geekarist/cljfx-open-uri
- The application has a 'URI' text field and an 'Open URI' button 
- The initial `open-uri!` effect function which is triggered when clicking the button appends a suffix (`#hello`) to the URI, and opens it in the desktop browser
- At the end of `core.clj`, there is a `defn` to redefine `open-uri!` so that it appends `#hello-cljfx` instead of just `#hello` to the URI
- Then the `renderer` function is called to re-render the app as explained in `e12_interactive_development.clj`

To reproduce:
- Start a REPL in the example project and connect (jack-in)
- A window opens, saying Hello
- Write an URI in the text field
- Click the 'Open URI' button
- Expected: the browser opens your URI with a `#hello-cljfx` suffix
- Result: it opens with a `#hello` suffix. That indicates that the effect function has not been reloaded.

### Solution

Instead of defining the effects map like this:

```clojure
(def effects
  {:eff/log log!
   :eff/open-uri open-uri!})
```

Define it like this:

```clojure
(def effects
  {:eff/log #(log! %1 %2)
   :eff/open-uri #(open-uri! %1 %2)})
```
