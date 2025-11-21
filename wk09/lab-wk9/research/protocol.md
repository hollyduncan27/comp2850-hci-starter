# Protocol (Peers, no PII)
Order: warm-up → T1 → UMUX-Lite Q1 → T2 → UMUX-Lite Q2 → T3 → debrief.
JS on; repeat one task with JS off. 3 minutes per task cap. Minimal prompting.

# Peer Pilot Protocol — Week 9

## Study Overview

**Purpose**: Evaluate usability and accessibility of task list prototype with peer participants.

**Type**: Low-risk formative evaluation, peer-to-peer within module.

**Scope**:
- 5–6 participants (lab pairs)
- 4 tasks per session (~15–20 minutes)
- No audio/video recording
- No personally identifiable information collected

**Ethical approval**: Covered by module's blanket low-risk consent for peer learning activities (verified with module leader).

**Data retention**: Anonymised logs stored in private repo for academic year, deleted after module assessment complete.

---

## Participant Requirements

**Inclusion**:
- Enrolled in COMP2850
- Comfortable using web browsers
- Able to provide informed consent

**Exclusion**:
- None (module is inclusive by design)

**Accessibility accommodations**:
- Screen reader users: allowed extra time, SR-specific observations recorded separately
- Keyboard-only users: explicitly invited to test no-mouse variant
- No-JS users: at least one session conducted with JS disabled

---

## Consent Process

**Before starting** (read aloud):

> "Thanks for agreeing to pilot our prototype. This is a quick usability test—about 15 minutes. I'll ask you to complete 4 tasks while I observe and take notes. I'm testing the interface, not you, so there are no wrong answers.
>
> **What we're collecting**:
> - Task completion times (from server logs)
> - Whether you complete each task successfully
> - Errors or validation issues
> - Your confidence ratings after each task
> - My notes on any hesitations or accessibility issues
>
> **What we're NOT collecting**:
> - Your name, email, or student ID
> - Screen recordings or audio
> - Your device details beyond 'keyboard-only' or 'screen reader'
>
> I'll assign you a random session code (like `sid=X7kL9p`) which will appear in the logs. You can request that I delete all data linked to your session code at any time, even after today.
>
> **You can stop at any time**, no questions asked, and it won't affect your grade.
>
> Do you have any questions before we start?"

**Verbal consent**: "Are you happy to proceed?"

Record in `wk09/lab-wk9/research/consent-log.md`:

**Opt-out path**: If participant requests deletion:
1. Open `data/metrics.csv`
2. Delete all rows where `session_id=X7kL9p`
3. Note in `consent-log.md`: "Data deleted on request [date]"

---

## Session Setup

**Environment**:
- Quiet space in lab (not open-plan area)
- Participant laptop/desktop with browser open to prototype
- Facilitator laptop for notes (don't share screen)

**Pre-pilot**:
1. Generate random session ID: `openssl rand -hex 3` → e.g., `7a9f2c`
2. Set cookie in participant browser:
   ```javascript
   document.cookie = "sid=7a9f2c; path=/";