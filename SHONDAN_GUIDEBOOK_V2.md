# SHONDAN — Complete Build Guidebook
### Version 2.0 | Using Firebase MCP inside Windsurf (Antigravity)

---

## How to Read This Guide

Every single step is labeled. Never skip a step.

| Label | Meaning |
|-------|---------|
| 🙋 YOU | You do this manually — clicking, downloading, saving |
| 🤖 WINDSURF | Copy the prompt exactly and paste into Windsurf AI chat |
| ⚠️ WARNING | Critical — do not skip or rush this |
| ✅ CHECK | Verify this before moving to the next step |

---

## What Firebase MCP Means for You

Normally you would go to Firebase Console manually and create
databases, set rules, and configure everything yourself.

With **Firebase MCP connected inside Windsurf**, you skip all of that.
Windsurf talks directly to your Firebase project and does it all for you.
You just paste the prompts. Windsurf creates collections, sets security
rules, configures authentication — everything — without you touching
the Firebase Console at all.

---

## Final Tech Stack

| What | Tool |
|------|------|
| UI Design | Stitch |
| Code Editor | Windsurf (Antigravity) |
| App Framework | React + Vite |
| Backend + Database + Auth | Firebase (via Firebase MCP in Windsurf) |
| URL Scraping | Apify API |
| Fact Checking AI | Claude API |
| Chatbot | Gemini API |
| APK Packaging | Capacitor |

---

---

# PHASE 0 — ACCOUNTS AND KEYS
## 🙋 All manual. Just create accounts and save keys.

---

## 🙋 STEP 1 — Create Your Firebase Project

1. Go to **https://console.firebase.google.com**
2. Sign in with your Google account
3. Click **Add project**
4. Name it: `shondan`
5. Turn off Google Analytics — not needed
6. Click **Create project**
7. Wait about 30 seconds

**Enable Email/Password Login:**
1. Left menu → click **Authentication**
2. Click **Get started**
3. Click **Email/Password**
4. Toggle **Enable** to ON
5. Click **Save**

**Enable Firestore Database:**
1. Left menu → click **Firestore Database**
2. Click **Create database**
3. Choose **Start in production mode**
4. Pick the region nearest to you
5. Click **Enable**

**Copy your Firebase config:**
1. Click the gear icon ⚙️ → **Project settings**
2. Scroll down to **Your apps**
3. Click the **</>** web icon
4. Nickname: `shondan-web` → click **Register app**
5. You will see a block of code — copy and save ALL of these values:

```
apiKey
authDomain
projectId
storageBucket
messagingSenderId
appId
```

⚠️ Save these in a text file on your Desktop called `shondan-keys.txt`

---

## 🙋 STEP 2 — Get Claude API Key

1. Go to **https://platform.anthropic.com**
2. Sign up with your email
3. Left menu → **API Keys**
4. Click **Create Key** → name it `shondan`
5. **Copy the key immediately — you cannot see it again**
6. Paste it into your `shondan-keys.txt` file
7. You get ~$5 free credits automatically ✅

---

## 🙋 STEP 3 — Get Gemini API Key

1. Go to **https://aistudio.google.com**
2. Sign in with Google
3. Click **Get API Key** → **Create API key**
4. Copy and save it in `shondan-keys.txt`
5. Free tier: 1,500 requests per day — no credit card needed ✅

---

## 🙋 STEP 4 — Get Apify Token

1. Go to **https://apify.com**
2. Sign up — no credit card needed
3. Go to **Settings → Integrations**
4. Copy your **API Token**
5. Save it in `shondan-keys.txt` ✅

---

## 🙋 STEP 5 — Install Node.js

1. Go to **https://nodejs.org**
2. Download the **LTS** version
3. Install like a normal program
4. Open Terminal
   - Windows: press `Win + R`, type `cmd`, press Enter
   - Mac: search Terminal in Spotlight
5. Type this and press Enter:
```
node --version
```
6. You should see something like `v20.11.0` ✅

---

## 🙋 STEP 6 — Install Android Studio

1. Go to **https://developer.android.com/studio**
2. Download and install it
3. Open it — let it finish installing SDK tools automatically
4. This takes 10–15 minutes the first time
5. Once you see the Android Studio welcome screen — done ✅

---

## 🙋 STEP 7 — Install Windsurf

1. Go to **https://codeium.com/windsurf**
2. Download for your operating system
3. Install and open Windsurf
4. Create a free account and sign in

---

## 🙋 STEP 8 — Connect Firebase MCP inside Windsurf

This is the key step that lets Windsurf talk directly to your Firebase.

1. Open Windsurf
2. Click the **MCP** icon or go to **Settings → MCP Servers**
3. Click **Add MCP Server**
4. Search for **Firebase**
5. Select **Firebase MCP**
6. When it asks for your Firebase project ID — paste it from your `shondan-keys.txt`
7. Sign in with your Google account when prompted
8. You should see Firebase MCP listed as **Connected** ✅

⚠️ If you cannot find Firebase MCP in Windsurf's MCP panel, paste this into Windsurf chat:
```
How do I connect Firebase MCP to this Windsurf project?
Walk me through it step by step.
```

---

## 🙋 STEP 9 — Design in Stitch and Export

1. Go to Stitch
2. Paste the Stitch design prompt (from your SHONDAN_DESIGN.md file)
3. Let Stitch generate all 15 screens
4. Review and approve the design
5. Click **Export** → download as **ZIP**
6. Extract the ZIP to your Desktop
7. Note the folder path — you will need it later ✅

---

## 🙋 STEP 10 — Create Project Folder

1. Create a new folder on your Desktop called `shondan`
2. Open Windsurf
3. Go to **File → Open Folder**
4. Select the `shondan` folder
5. You are now inside your project ✅

---

## 🙋 STEP 11 — Create Environment File

1. In Windsurf, right-click the file panel on the left
2. Click **New File**
3. Name it exactly: `.env`
4. Paste this and fill in your real values from `shondan-keys.txt`:

```
VITE_FIREBASE_API_KEY=paste_here
VITE_FIREBASE_AUTH_DOMAIN=paste_here
VITE_FIREBASE_PROJECT_ID=paste_here
VITE_FIREBASE_STORAGE_BUCKET=paste_here
VITE_FIREBASE_MESSAGING_SENDER_ID=paste_here
VITE_FIREBASE_APP_ID=paste_here
VITE_ANTHROPIC_API_KEY=paste_here
VITE_GEMINI_API_KEY=paste_here
VITE_APIFY_TOKEN=paste_here
```

5. Press **Ctrl+S** to save ✅

---

---

# PHASE 1 — WINDSURF SETS UP FIREBASE VIA MCP
## 🤖 Paste each prompt into Windsurf AI chat. Wait for it to finish before the next one.

---

## 🤖 STEP 12 — Initialize the Project

```
I am building an app called Shondan.
Initialize a new React project using Vite in the 
current folder. Run:

npm create vite@latest . -- --template react

Then install these packages:

npm install
npm install firebase
npm install @google/generative-ai
npm install react-router-dom
npm install axios
npm install react-dropzone
npm install lucide-react
npm install @capacitor/core @capacitor/cli @capacitor/android

After installing, show me the folder structure.
```

---

## 🤖 STEP 13 — Configure Firebase via MCP

```
I have Firebase MCP connected to this Windsurf project.
My Firebase project ID is in the .env file as 
VITE_FIREBASE_PROJECT_ID.

Using Firebase MCP, do all of the following:

1. Enable Email/Password authentication in Firebase Auth
   if not already enabled.

2. Create the Firestore security rules so that:
   - A user can only read and write their own 
     document in the users collection
   - A user can only read and write documents inside
     their own searches subcollection
   Rules should be:

   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId} {
         allow read, write: if request.auth != null 
           && request.auth.uid == userId;
         match /searches/{searchId} {
           allow read, write: if request.auth != null 
             && request.auth.uid == userId;
         }
       }
     }
   }

3. Create the Firestore indexes needed for:
   - Querying searches by input_type field
   - Sorting searches by createdAt descending

4. Confirm all of the above is done and show me 
   the current Firebase project status.
```

---

## 🤖 STEP 14 — Set Up Firebase in the App Code

```
Create the Firebase configuration files for Shondan.

FILE 1: src/lib/firebase.js

Initialize Firebase using these environment variables:
VITE_FIREBASE_API_KEY
VITE_FIREBASE_AUTH_DOMAIN
VITE_FIREBASE_PROJECT_ID
VITE_FIREBASE_STORAGE_BUCKET
VITE_FIREBASE_MESSAGING_SENDER_ID
VITE_FIREBASE_APP_ID

Export:
- app (Firebase app instance)
- auth (Firebase Auth)
- db (Firestore)

FILE 2: src/context/AuthContext.jsx

React context that:
- Listens to Firebase auth state changes with onAuthStateChanged
- Exposes: user, loading, logout
- logout calls Firebase signOut then redirects to /login
- Shows nothing while loading (prevents flash)

FILE 3: src/components/ProtectedRoute.jsx

- If loading: show a centered screen with "SHONDAN" 
  text pulsing gently on dark background
- If no user: redirect to /login
- If user logged in: show children components

FILE 4: src/lib/firestore.js

Export these helper functions that use Firebase MCP:

saveSearch(userId, searchData)
  - Creates a new document in users/{userId}/searches
  - searchData contains: input_type, original_input,
    claim, verdict, confidence, explanation, 
    sources, summary (null initially), createdAt

updateSearchSummary(userId, searchId, summaryText)
  - Updates the summary field of a specific search document

getUserSearches(userId, inputType)
  - Fetches all documents from users/{userId}/searches
  - Filters by input_type
  - Sorts by createdAt descending
  - Returns array of search objects

getSearchCounts(userId)
  - Returns count of searches for each input_type
  - Returns: { url: number, image: number, text: number }
```

---

## 🤖 STEP 15 — Use Firebase MCP to Create Test Data

```
Using Firebase MCP, create a test document in Firestore
to confirm the database is working.

In the collection path: test/connection
Create a document with field:
  status: "Firebase MCP is working"
  createdAt: current timestamp

Then read it back and show me the value.
Then delete it.

If this works, Firebase MCP is fully connected. ✅
If it fails, show me the error and fix it.
```

---

---

# PHASE 2 — BUILD ALL SCREENS
## 🤖 Continue pasting prompts into Windsurf

---

## 🤖 STEP 16 — Global Styles

```
Create src/styles/global.css with this content:

@import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Syne:wght@600;700;800&family=DM+Sans:wght@400;500&family=JetBrains+Mono:wght@400&display=swap');

:root {
  --bg: #08080E;
  --surface: #111118;
  --surface-raised: #1A1A24;
  --border: #252533;
  --border-bright: #3A3A50;
  --primary: #5B8DF6;
  --primary-glow: rgba(91,141,246,0.2);
  --true: #16A34A;
  --true-glow: rgba(22,163,74,0.15);
  --false: #DC2626;
  --false-glow: rgba(220,38,38,0.15);
  --misleading: #D97706;
  --misleading-glow: rgba(217,119,6,0.15);
  --text: #F0F0F8;
  --text-secondary: #6B6B80;
  --text-tertiary: #3D3D52;
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 24px;
  --radius-full: 999px;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  -webkit-tap-highlight-color: transparent;
}

html, body, #root {
  height: 100%;
}

body {
  background: var(--bg);
  color: var(--text);
  font-family: 'DM Sans', sans-serif;
  max-width: 430px;
  margin: 0 auto;
  min-height: 100vh;
  overflow-x: hidden;
  -webkit-font-smoothing: antialiased;
}

input, textarea, button {
  font-family: 'DM Sans', sans-serif;
  outline: none;
  border: none;
}

a {
  color: var(--primary);
  text-decoration: none;
}

::-webkit-scrollbar {
  width: 0px;
}

Then import this file in src/main.jsx at the top.
```

---

## 🤖 STEP 17 — Login Page

```
Build src/pages/LoginPage.jsx

Full screen, dark background #08080E, centered content.

TOP SECTION:
- 80px space from top
- A magnifying glass icon (from lucide-react: Search)
  Size 48px, color #5B8DF6, with a soft blue glow 
  underneath it using box-shadow or filter
- Text "SHONDAN" font-family Bebas Neue, 
  font-size 42px, color white, letter-spacing 3px
- Text "Real-Time AI Fact Checker" 
  DM Sans 14px, color #6B6B80, margin-top 8px
- A thin horizontal line (#252533) with 48px margin top and bottom

FORM SECTION:
- Label "Email" DM Sans 12px #6B6B80, margin-bottom 6px
- Email input field:
  width 100%, background #1A1A24, border 1px solid #252533,
  border-radius 12px, padding 14px 16px, color white,
  placeholder color #3D3D52, font-size 14px
  On focus: border-color changes to #5B8DF6

- 16px gap

- Label "Password" same style as above
- Password input with eye icon inside it on the right
  clicking eye toggles password visibility

- "Forgot password?" text right-aligned below password
  DM Sans 12px, color #3D3D52

- 24px gap

- Sign In button:
  width 100%, height 52px, background #5B8DF6,
  color white, Syne 600 15px, border-radius 12px,
  box-shadow: 0 0 24px rgba(91,141,246,0.3)
  On press: scale(0.97)
  While loading: show spinner and text "Signing in..."

- Error message area below button:
  DM Sans 13px, color #DC2626, text-align center
  Only visible when there is an error

- 32px gap

- Text "Don't have an account?" DM Sans 13px #6B6B80
  Followed by link "Create one →" color #5B8DF6
  Clicking navigates to /register

BACKGROUND DETAIL:
Very subtle dot grid pattern behind everything.
Use CSS: 
background-image: radial-gradient(#ffffff06 1px, transparent 1px);
background-size: 24px 24px;

LOGIC:
On Sign In click:
- Validate email and password not empty
- Call Firebase signInWithEmailAndPassword(auth, email, password)
- On success: navigate to /home
- On error: show readable error message
  "Wrong password" for wrong-credentials
  "No account found" for user-not-found
  "Too many attempts. Try later." for too-many-requests
```

---

## 🤖 STEP 18 — Register Page

```
Build src/pages/RegisterPage.jsx

Same dark theme as login page.

HEADER:
- Back arrow (ChevronLeft from lucide-react) top left
  clicking goes to /login
- No app name here

CONTENT:
- Title "Create Account" Syne 700 26px white
- Subtitle "Join Shondan" DM Sans 14px #6B6B80
- 32px gap

FORM:
- Label + Input: Full Name
  placeholder "Your full name"

- Label + Input: Email
  placeholder "your@email.com"
  type email

- Label + Input: Password
  placeholder "Create a password"
  type password with eye toggle

- Label + Input: Confirm Password
  placeholder "Repeat your password"
  type password with eye toggle

All inputs same style as login page.

- 24px gap

- "Create Account" button — same style as Sign In button

- Error message area (same style as login)

- 24px gap

- "Already have an account?" text + "Sign in →" link

LOGIC:
On Create Account click:
- Check full name not empty
- Check email valid
- Check password at least 6 characters
- Check confirm password matches password
- Show specific error if any check fails

If all valid:
- Call Firebase createUserWithEmailAndPassword
- Using Firebase MCP, create a Firestore document at:
  users/{newUserId}
  with fields:
    fullName: (what they typed)
    email: (what they typed)
    createdAt: serverTimestamp()
- On success: navigate to /home
- On error: show "This email is already registered"
```

---

## 🤖 STEP 19 — Home Page

```
Build src/pages/HomePage.jsx

This is the main screen. Build it in sections.

SECTION 1 — STICKY HEADER:
Height 56px, background #08080E, 
border-bottom 1px solid #252533, 
position sticky, top 0, z-index 100,
display flex, align-items center, 
justify-content space-between, padding 0 16px.

Left side: Search icon (16px #5B8DF6) + 
text "SHONDAN" Bebas Neue 22px white, gap 8px

Right side: circular avatar button (36px diameter)
background #1A1A24, border 1px solid #252533,
shows user initial letter from their name
Clicking shows a small dropdown with:
  - User full name, DM Sans 14px white
  - User email, DM Sans 12px #6B6B80  
  - Divider line
  - "Log Out" button, color #DC2626

SECTION 2 — INPUT TABS:
Three equal-width tabs below header:
🔗 URL | 🖼️ Image | 📝 Text

Tab styling:
- Height 44px
- Active: text #5B8DF6, bottom border 2px solid #5B8DF6
- Inactive: text #6B6B80
- Font: DM Sans 500 13px
- Bottom border on container: 1px solid #252533

SECTION 3 — INPUT AREA (switches per tab):
Padding 16px, background transparent.

URL TAB:
- Input field, full width
  placeholder "Paste a link here..."
  All input styles from global
- Text below input: 
  "Works with news and blogs. Facebook links may vary."
  DM Sans 11px #3D3D52, margin-top 6px

IMAGE TAB:
- Upload box, full width, height 160px
  Background #111118
  Border 2px dashed #252533
  Border-radius 16px
  Centered content:
    Image icon (ImageIcon from lucide-react) 
    40px color #3D3D52
    "Tap to upload a screenshot" DM Sans 14px #6B6B80
    "JPG, PNG or WEBP" DM Sans 12px #3D3D52
  Clicking opens file picker
  After image selected: show thumbnail preview 
  (cover fit) with X button top right to remove

TEXT TAB:
- Textarea, full width, min-height 140px
  placeholder "Paste or type the claim to fact-check..."
  Same styles as input fields
  No resize

SECTION 4 — FACT CHECK BUTTON:
Margin 0 16px, full width minus padding.
Height 52px, background #5B8DF6
"🔍 Fact Check" Syne 600 15px white
Border-radius 12px
Box-shadow: 0 0 24px rgba(91,141,246,0.25)
Active: scale(0.97)

While loading:
- Show spinning loader icon instead of emoji
- Text: "Checking..."
- Below button: "Searching the web for evidence..." 
  DM Sans 12px #6B6B80 text-align center
- Button is disabled

SECTION 5 — RESULT AREA:
Hidden by default. Appears with fade-in + slide-up 
20px animation (300ms) after result arrives.
Padding 16px, border-top 1px solid #252533, margin-top 8px.

VERDICT BADGE (centered):
Pill shape, padding 8px 24px, border-radius 999px
Syne 800 20px, margin-bottom 16px

TRUE: background var(--true-glow), 
  color var(--true), border 1px solid #16A34A40
  Text: ✅ TRUE

FALSE: background var(--false-glow),
  color var(--false), border 1px solid #DC262640
  Text: ❌ FALSE

MISLEADING: background var(--misleading-glow),
  color var(--misleading), border 1px solid #D9770640
  Text: ⚠️ MISLEADING

CONFIDENCE BAR:
Track: full width, height 6px, #1A1A24, radius 999px
Fill: verdict color, width animates from 0% to 
  confidence% over 600ms ease-out, delayed 200ms
Below bar: "94% confident this is FALSE"
  DM Sans 13px #6B6B80, margin-top 8px

SOURCES SECTION:
Title "Sources" Syne 600 13px #6B6B80, 
margin 20px 0 10px 0
Each source:
  External link icon (ExternalLink from lucide-react)
  12px, color = verdict color
  Source title or URL, JetBrains Mono 12px #6B6B80
  Tapping opens URL in browser
  Padding 8px 0, border-bottom 1px solid #1A1A24

TWO ACTION BUTTONS (side by side, gap 10px):
Margin-top 20px.

"📋 Get Summary":
  Flex 1, height 44px
  Background transparent
  Border 1px solid #3A3A50
  Color #F0F0F8, DM Sans 500 13px
  Border-radius 12px
  Active: background #1A1A24

"💬 Chat with Shondan":
  Same styles

SECTION 6 — SUMMARY PANEL (slide-up sheet):
Triggered by Get Summary button.
Dim overlay: fixed, full screen, #00000080, z-index 200
Panel: fixed, bottom 0, left 0, right 0 (max-width 430px centered)
  Background #111118
  Border-radius 24px 24px 0 0
  Max-height 80vh
  Padding 0 20px 32px 20px
  Slides up with transform: translateY animation 300ms

Inside panel:
  Drag handle: 40x4px, background #3A3A50, 
  border-radius 999px, centered, margin 12px auto 20px

  Header row: "Summary" Syne 700 18px white left,
  X button (X icon from lucide-react) right
  color #6B6B80, 24px icon, clicking closes panel

  Content area (scrollable):
  While loading: centered text "Generating summary..." 
  color #6B6B80, with three animated dots

  When loaded: numbered sentences 1 through 10
  Each: flex row, gap 12px
  Number: Syne 700 15px color #5B8DF6, min-width 20px
  Sentence: DM Sans 14px #F0F0F8, line-height 1.7
  Gap between sentences: 16px

SECTION 7 — CHAT PANEL (slide-up sheet):
Same overlay and animation as Summary panel.

Inside panel:
  Drag handle (same style)

  Header row:
  Left: "Shondan AI" Syne 700 18px white + 
    robot emoji 🤖
  Below title: "Has context of your fact-check"
    DM Sans 11px #6B6B80
  Right: X close button

  Chat messages area (scrollable, flex-grow):
  Padding 16px 0

  AI message bubble:
  Align left, max-width 85%
  Background #1A1A24, border-radius 4px 16px 16px 16px
  Padding 12px 14px, DM Sans 14px #F0F0F8
  Margin-bottom 12px

  User message bubble:
  Align right, max-width 85%
  Background #5B8DF620, border 1px solid #5B8DF640
  Border-radius 16px 4px 16px 16px
  Padding 12px 14px, DM Sans 14px #F0F0F8
  Margin-bottom 12px

  Typing indicator (three dots bouncing) shown while 
  waiting for AI response.

  Bottom bar (sticky at bottom of panel):
  Flex row, gap 10px, padding-top 12px
  border-top 1px solid #252533

  Input: flex-grow, background #1A1A24, 
  border 1px solid #252533, radius 12px,
  padding 12px 14px, color white, DM Sans 14px
  placeholder "Ask anything about this..."

  Send button: 44x44px, background #5B8DF6,
  border-radius 12px, Send icon from lucide-react
  white 18px

BOTTOM NAVIGATION BAR:
Fixed at bottom, height 60px + safe area padding
Background #111118, border-top 1px solid #252533
Full width (max 430px centered)
Two items centered with equal spacing:

🏠 Home (using Home icon from lucide-react)
  Label "Home" DM Sans 500 11px
  Active: icon + label color #5B8DF6

📋 History (using Clock icon from lucide-react)
  Label "History" DM Sans 500 11px
  Inactive: icon + label color #6B6B80

Clicking History navigates to /history
Icon size 22px
```

---

## 🤖 STEP 20 — History Page

```
Build src/pages/HistoryPage.jsx

HEADER:
Height 56px, same sticky style as HomePage header.
Left: ChevronLeft icon, clicking goes to /home
Center: "My History" Syne 700 20px white
Right: empty (for balance)

CATEGORY TABS:
Three tabs: 🔗 URL | 🖼️ Image | 📝 Text
Same tab styling as HomePage.

Each tab has a count badge:
Small pill next to the label text.
Background #5B8DF620, color #5B8DF6
DM Sans 500 11px, padding 2px 8px, radius 999px
Shows number of searches in that category.

SEARCH LIST:
Padding 16px, gap 12px between cards.

On mount: fetch all searches from Firestore using 
getUserSearches(userId, activeTab)
Show loading skeleton (3 placeholder cards 
with pulse animation) while fetching.

Each card:
Background #111118, border 1px solid #252533
Border-radius 16px, padding 16px
Transition on tap: background to #1A1A24

Card row 1:
Left: verdict badge (small, compact version)
  TRUE: small green pill ✅ TRUE
  FALSE: small red pill ❌ FALSE  
  MISLEADING: small amber pill ⚠️ MISLEADING
  DM Sans 500 11px, padding 3px 10px, radius 999px
Right: confidence number, Bebas Neue 18px, verdict color

Card row 2:
Source identifier (URL, "Screenshot", or first words of text)
JetBrains Mono 11px #6B6B80
One line max, overflow ellipsis

Card row 3:
Claim text, DM Sans 13px #F0F0F8
Two lines max, overflow ellipsis with ...

Card row 4:
Date and time formatted as "May 10, 2025 · 3:42 PM"
DM Sans 11px #3D3D52

EXPANDED CARD STATE (tapped):
Tapping a card expands it in place (no navigation).
A ChevronDown arrow on each card rotates to ChevronUp 
when expanded.

Expanded content shows below card row 4:
Divider line #252533

"Claim" label Syne 600 12px #6B6B80
Full claim text, DM Sans 14px white, margin-bottom 16px

"Sources" label same style
Each source as clickable link with ↗ icon
JetBrains Mono 12px, margin-bottom 8px

If summary exists:
"Summary" label same style
First 3 sentences shown, DM Sans 13px white

"🔄 Re-check this" button:
Full width, outlined style, height 44px
Clicking navigates to /home and pre-fills 
the claim in the Text tab

EMPTY STATE:
If no searches in active tab:
Centered content: search icon (48px #3D3D52)
Text "No [URL / image / text] checks yet"
DM Sans 14px #3D3D52

BOTTOM NAV: same as HomePage, History tab active.

When tab is switched: re-fetch searches for new tab.
```

---

---

# PHASE 3 — AI LOGIC
## 🤖 Building the fact-checking brain

---

## 🤖 STEP 21 — Apify URL Scraper

```
Create src/lib/scraper.js

Export one async function: scrapeUrl(url)

Logic:

CASE 1 — URL contains "facebook.com":
  Make a POST request to Apify API:
  URL: https://api.apify.com/v2/acts/apify~facebook-posts-scraper/run-sync-get-dataset-items?token=${VITE_APIFY_TOKEN}
  
  Body:
  {
    "startUrls": [{ "url": url }],
    "maxPosts": 1,
    "maxPostComments": 0
  }

  From the response array, extract the first item's 
  "text" or "message" field.

  If text found and not empty:
    return { success: true, text: extractedText }
  
  If empty or request fails:
    return { 
      success: false, 
      error: "Could not read this Facebook post. It may be private or restricted. Please copy the post text and use the Text tab instead." 
    }

CASE 2 — Any other URL:
  Call Claude API (claude-haiku-20240307):
  Endpoint: https://api.anthropic.com/v1/messages
  Headers:
    x-api-key: VITE_ANTHROPIC_API_KEY
    anthropic-version: 2023-06-01
    content-type: application/json

  Message: 
  "Visit this URL and extract the main claim, 
  headline, or key statement: [url]
  Return only the extracted text, nothing else, 
  no explanations."

  Return { success: true, text: responseText }
  
  On failure:
  Return { 
    success: false, 
    error: "Could not read this URL. Please paste the content in the Text tab." 
  }

Wrap everything in try/catch.
Use environment variables via import.meta.env
```

---

## 🤖 STEP 22 — Claude Fact Checker

```
Create src/lib/factChecker.js

Export two async functions:

FUNCTION 1: factCheck(claimText)

Call Claude API:
  Endpoint: https://api.anthropic.com/v1/messages
  Model: claude-3-5-sonnet-20241022
  Max tokens: 1500
  
  Include the web_search tool so Claude can 
  search the internet before concluding.

  System prompt:
  "You are Shondan, a professional fact-checking AI.
  Your job is to verify claims by searching the web.
  Always search before concluding.
  Be neutral, thorough, and cite real sources.
  Respond ONLY with valid JSON, nothing else."

  User message:
  "Fact-check this claim: [claimText]
  
  Search the web for evidence.
  Then respond with ONLY this JSON structure, 
  no extra text before or after:
  {
    'verdict': 'TRUE' or 'FALSE' or 'MISLEADING',
    'confidence': number between 50 and 99,
    'explanation': 'two to three sentences explaining your verdict',
    'sources': [
      { 'title': 'source name', 'url': 'https://full-url' },
      { 'title': 'source name', 'url': 'https://full-url' },
      { 'title': 'source name', 'url': 'https://full-url' }
    ],
    'claim': 'restate the exact claim you checked'
  }"

Parse the JSON response and return it.
Handle JSON parse errors with a fallback error object.

FUNCTION 2: generateSummary(claim, verdict, confidence, explanation, sources)

Call Claude API:
  Model: claude-haiku-20240307
  Max tokens: 600

  Message:
  "Write exactly 10 sentences summarizing this 
  fact-check. Number each sentence. One sentence 
  per line.
  
  Claim checked: [claim]
  Verdict: [verdict] with [confidence]% confidence
  Explanation: [explanation]
  Sources: [sources mapped to title + url]
  
  Make each sentence informative and clear.
  Start with the verdict, explain the evidence,
  give context, end with what readers should know."

Return the response as a plain string.
Split by newline to get array of 10 sentences 
for rendering.
```

---

## 🤖 STEP 23 — Gemini Chatbot

```
Create src/lib/chatbot.js

Export one function: createChat(factCheckResult)

Where factCheckResult has:
  claim, verdict, confidence, explanation, sources

Use @google/generative-ai package:
  import { GoogleGenerativeAI } from "@google/generative-ai"
  
  const genAI = new GoogleGenerativeAI(VITE_GEMINI_API_KEY)
  const model = genAI.getGenerativeModel({ 
    model: "gemini-2.0-flash" 
  })

Start a chat session with history pre-loaded:
  history: [
    {
      role: "user",
      parts: [{ text: "I just fact-checked a claim." }]
    },
    {
      role: "model", 
      parts: [{ text: "I have reviewed the fact-check 
        on this claim: '[claim]'. The verdict is 
        [VERDICT] with [confidence]% confidence. 
        [explanation] What would you like to know?" }]
    }
  ]

Return an object with:
  initialMessage: the pre-loaded AI response above
  sendMessage: async function(userText) that calls
    chat.sendMessage(userText) and returns the 
    response text

Handle errors: if Gemini fails, return
  "I'm having trouble connecting right now. 
   Please try again."
```

---

## 🤖 STEP 24 — Image Text Extraction

```
Create src/lib/imageReader.js

Export one async function: extractTextFromImage(base64ImageData, mimeType)

Call Claude API with vision:
  Model: claude-3-5-sonnet-20241022
  
  Message content: array with two items:
  1. Image object:
    type: "image"
    source:
      type: "base64"
      media_type: mimeType (e.g. "image/jpeg")
      data: base64ImageData

  2. Text object:
    type: "text"
    text: "Extract ALL text visible in this image 
    exactly as it appears. Include every word, 
    number, and phrase you can see. Return only 
    the extracted text, nothing else."

Return the extracted text as a string.

On failure return:
  { error: "Could not read text from this image. 
    Please make sure the image is clear and 
    contains readable text." }
```

---

## 🤖 STEP 25 — Connect All Logic to Home Page

```
Now wire up all the logic inside HomePage.jsx.

Import these:
- scrapeUrl from src/lib/scraper.js
- factCheck, generateSummary from src/lib/factChecker.js
- createChat from src/lib/chatbot.js
- extractTextFromImage from src/lib/imageReader.js
- saveSearch, updateSearchSummary from src/lib/firestore.js
- auth and db from src/lib/firebase.js
- useAuth from src/context/AuthContext.jsx

STATE VARIABLES NEEDED:
- activeTab: 'url' | 'image' | 'text'
- urlInput: string
- imageFile: File | null
- imagePreview: string | null
- textInput: string
- isLoading: boolean
- result: object | null (the fact-check result)
- currentSearchId: string | null (Firestore doc id)
- showSummaryPanel: boolean
- summaryText: string | null
- isSummaryLoading: boolean
- showChatPanel: boolean
- chatSession: object | null
- chatMessages: array
- chatInput: string
- isChatLoading: boolean
- dropdownOpen: boolean

WHEN FACT CHECK IS CLICKED:
Step 1: Set isLoading true, clear previous result

Step 2: Get claim text based on active tab:
  URL tab:
    If empty: show error "Please paste a URL"
    Call scrapeUrl(urlInput)
    If success false: show error message, stop
    claimText = result.text

  Image tab:
    If no image: show error "Please upload an image"
    Convert imageFile to base64 using FileReader
    Call extractTextFromImage(base64, mimeType)
    If error: show error message, stop
    claimText = extracted text

  Text tab:
    If empty: show error "Please enter some text"
    claimText = textInput

Step 3: Call factCheck(claimText)
  If fails: show "Fact check failed. Please try again."
  Set result = response

Step 4: Save to Firestore using saveSearch:
  userId = auth.currentUser.uid
  data = {
    input_type: activeTab,
    original_input: (url / image filename / first 60 chars of text),
    claim: result.claim,
    verdict: result.verdict,
    confidence: result.confidence,
    explanation: result.explanation,
    sources: result.sources,
    summary: null,
    createdAt: serverTimestamp()
  }
  Save the returned document ID as currentSearchId

Step 5: Set isLoading false, show result section

WHEN GET SUMMARY IS CLICKED:
  Set showSummaryPanel true
  Set isSummaryLoading true
  Call generateSummary(result.claim, result.verdict, 
    result.confidence, result.explanation, result.sources)
  Set summaryText = response
  Set isSummaryLoading false
  If currentSearchId exists: call updateSearchSummary

WHEN CHAT IS CLICKED:
  Set showChatPanel true
  If chatSession is null:
    Call createChat(result)
    Set chatSession = returned session
    Add initial AI message to chatMessages

WHEN USER SENDS CHAT MESSAGE:
  Add user message to chatMessages
  Set isChatLoading true
  Call chatSession.sendMessage(userInput)
  Add AI response to chatMessages
  Set isChatLoading false
  Clear chatInput
  Auto-scroll chat to bottom

FOR IMAGE UPLOAD:
  When file selected: 
    Create preview URL with URL.createObjectURL
    Store file in imageFile state
  When X clicked: clear imageFile and imagePreview

Make sure all panels (summary, chat) close properly
with the X button and clicking the dim overlay.
```

---

## 🤖 STEP 26 — Set Up Routing

```
Update src/App.jsx:

Set up React Router DOM with these routes:

/ → redirect to /login
/login → LoginPage (public)
/register → RegisterPage (public)
/home → HomePage wrapped in ProtectedRoute
/history → HistoryPage wrapped in ProtectedRoute

Wrap everything in AuthContext.Provider.

Add a loading state:
While Firebase auth is initializing (checking 
if user is already logged in), show:
Full screen #08080E background
Centered "SHONDAN" text Bebas Neue 36px white
Subtle pulse animation

This prevents the login page from flashing 
for already logged-in users.

Also handle: if logged-in user visits /login 
or /register → redirect to /home automatically.
```

---

## 🤖 STEP 27 — Test Locally

```
Run the development server:

npm run dev

Open http://localhost:5173 in the browser.

Test these things and tell me what happens for each:

1. Does the login page appear with the correct design?
2. Can you register a new account?
3. Does it redirect to home page after registration?
4. Can you log out and log back in?
5. Does the URL tab show the input field?
6. Paste a news article URL and click Fact Check — 
   does a result appear?
7. Does the verdict badge show correctly?
8. Does the confidence bar animate?
9. Does Get Summary open the panel with sentences?
10. Does Chat open and respond to a message?
11. Does History show the searches you made?
12. Do all three history tabs work?

Report any errors or things that look wrong.
```

---

## 🤖 STEP 28 — Fix All Issues

```
Based on the test results, fix every issue found.

For each problem:
1. Identify the root cause
2. Fix the code
3. Test again

Common things to check:
- Environment variables: make sure all use import.meta.env.VITE_
- Firebase connection: check console for auth errors
- API calls: check network tab for failed requests
- UI: check mobile layout at 390px width
- Panels: make sure they open and close correctly

Do not move to APK build until all 12 test 
items above are working correctly.
```

---

---

# PHASE 4 — BUILD THE APK
## Mix of Windsurf and manual steps

---

## 🤖 STEP 29 — Build and Configure Capacitor

```
Build the app for production and prepare the APK.

Run these commands in order:

1. npm run build
   (Builds the React app into the dist folder)

2. npx cap init "Shondan" "com.shondan.app" --web-dir dist
   (Initializes Capacitor)

3. npx cap add android
   (Adds Android platform)

4. npx cap copy android
   (Copies built files to Android folder)

Then make these file changes:

FILE: android/app/src/main/res/values/strings.xml
Change app_name value to: Shondan

FILE: android/app/src/main/AndroidManifest.xml
Add inside <manifest> tag:
<uses-permission android:name="android.permission.INTERNET"/>

FILE: capacitor.config.ts or capacitor.config.json
Make sure it has:
{
  "appId": "com.shondan.app",
  "appName": "Shondan",
  "webDir": "dist",
  "server": {
    "cleartext": true
  }
}

Then show me the command to open Android Studio 
with this project.
```

---

## 🙋 STEP 30 — Generate APK in Android Studio

1. Run the command Windsurf gave you in Terminal
2. Android Studio opens with the Shondan project
3. Wait for the bottom bar to say **"Gradle sync finished"**
   This can take 5–10 minutes the first time
4. In the top menu click **Build**
5. Click **Build Bundle(s) / APK(s)**
6. Click **Build APK(s)**
7. Wait 3–5 minutes
8. A notification appears: **"APK(s) generated successfully"**
9. Click **locate** in the notification
10. Find the file: `app-debug.apk` ✅

---

## 🙋 STEP 31 — Install APK on Your Android Phone

**Transfer the APK to your phone:**
- Via USB: connect phone, copy `app-debug.apk` to phone storage
- Via email: email it to yourself and download on phone
- Via WhatsApp: send to yourself on WhatsApp

**Install on phone:**
1. Open **Settings** on your phone
2. Go to **Apps** or **Security**
3. Find **Install unknown apps** or **Unknown sources**
4. Enable installation from your file manager
5. Open the APK file on your phone
6. Tap **Install**
7. Tap **Open**
8. Shondan is now installed on your phone ✅

---

---

# PHASE 5 — FINAL TESTING
## 🙋 Test on your actual phone

Test every item below on the installed app.
If anything fails, open Windsurf and describe the problem.

**Login and Registration:**
- [ ] Register with a new email
- [ ] Log out
- [ ] Log in again with same email
- [ ] Wrong password shows an error message
- [ ] Cannot register same email twice

**URL Fact Check:**
- [ ] Paste a BBC or Prothom Alo article URL
- [ ] Result appears with verdict and percentage
- [ ] Sources are clickable and open browser

**Facebook URL:**
- [ ] Paste a public Facebook post URL
- [ ] Either gets result OR shows helpful error message

**Image Fact Check:**
- [ ] Upload a screenshot with text
- [ ] Result appears automatically (no extra steps)

**Text Fact Check:**
- [ ] Type or paste a claim
- [ ] Result appears with verdict

**Summary:**
- [ ] Click Get Summary
- [ ] Panel slides up
- [ ] 10 numbered sentences appear

**Chat:**
- [ ] Click Chat with Shondan
- [ ] Panel slides up
- [ ] First AI message appears automatically
- [ ] Send a message and get a reply

**History:**
- [ ] All 3 tabs show correct searches
- [ ] Count badges show correct numbers
- [ ] Tapping a card expands it
- [ ] Re-check button works

**General:**
- [ ] App does not crash during normal use
- [ ] Bottom navigation works
- [ ] Log out works
- [ ] App works without internet (shows error gracefully)

---

# QUICK REFERENCE

## All Accounts and Where to Create Them

| Service | URL | What for |
|---------|-----|----------|
| Firebase | console.firebase.google.com | Auth + Database |
| Anthropic | platform.anthropic.com | Claude API (fact check) |
| Google AI Studio | aistudio.google.com | Gemini API (chatbot) |
| Apify | apify.com | Facebook URL scraping |

## Monthly Cost for Personal Use

| Service | Free Limit | Personal Cost |
|---------|-----------|---------------|
| Firebase | 50k users, 1GB storage | $0 |
| Claude API | ~$5 starter | ~$2–3/month |
| Gemini API | 1,500 req/day | $0 |
| Apify | $5 credits/month | $0 |
| **Total** | | **~$2–3/month** |

## When Something Breaks

Copy the error → Open Windsurf AI chat → Paste:
*"I got this error: [paste error here]. Fix it."*

You never need to write or understand code yourself.

---

*Shondan Guidebook v2.0*
*Stack: React + Vite + Firebase MCP + Claude API + Gemini API + Apify + Capacitor*
*Editor: Windsurf (Antigravity) | UI: Stitch*
