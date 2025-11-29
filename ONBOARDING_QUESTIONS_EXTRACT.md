# Onboarding Questions - Web App Extraction
## Source: https://nishchint-gamma.vercel.app/

**Status:** Ready for extraction from web app  
**Current Questions:** Based on app context (delivery worker financial management)

---

## 📝 CURRENT ONBOARDING QUESTIONS (MockData.kt)

### Question 1: Delivery Work
**Text:** "Kya tum delivery ka kaam karte ho? 🛵"  
**Options:**
- "Haan, Zomato/Swiggy"
- "Haan, Amazon/Flipkart"
- "Nahi, kuch aur"

### Question 2: Vehicle Ownership
**Text:** "Tumhara apna bike hai ya rent pe hai?"  
**Options:**
- "Apna hai"
- "Rent pe hai"
- "Nahi hai"

### Question 3: Monthly Petrol Expenses
**Text:** "Mahine mein kitna petrol lagta hai? ⛽"  
**Options:**
- "₹2000 se kam"
- "₹2000-4000"
- "₹4000 se zyada"

### Question 4: Loans/EMIs
**Text:** "Koi loan ya EMI chal rahi hai?"  
**Options:**
- "Haan, EMI hai"
- "Nahi, koi loan nahi"

### Question 5: Savings Goals
**Text:** "Kya save karna chahte ho? 🎯"  
**Options:**
- "Naya Bike"
- "Emergency Fund"
- "Family ke liye"
- "Kuch nahi socha"  
**Is Final:** `true`

---

## 🔄 ACTION REQUIRED

**Next Step:** Visit https://nishchint-gamma.vercel.app/ and extract:
1. Exact question text from web app
2. Exact option text
3. Question order
4. Any additional questions
5. Chat flow structure
6. Bot responses/messages

**Update:** Replace current questions in `MockData.kt` with exact web app questions.

---

## 📋 ONBOARDING FLOW STRUCTURE

### Expected Flow:
1. **Welcome Message** - Bot introduces itself
2. **Question 1** - User selects option
3. **Question 2** - User selects option
4. **Question 3** - User selects option
5. **Question 4** - User selects option
6. **Question 5** - User selects option (Final)
7. **Completion Message** - Summary/horoscope
8. **Navigate to Home** - Onboarding complete

### UI Elements:
- Chat-style interface
- User messages: Right-aligned, yellow background
- Bot messages: Left-aligned, dark background
- Option buttons: Yellow primary buttons
- Typing indicator: Animated dots
- Avatar: Yellow circular icon

---

**Note:** This document will be updated once exact questions are extracted from the web app.

