- Completion (0/1): finished within 180s
- Time-on-task (ms): request start→end
- Errors: validation, server
- A11y confirmations: keyboard-only, status, focus
- UMUX-Lite x2; Confidence 1–5

 Metrics Definitions — Week 9

Reference: [Evaluation Metrics Quick Reference](../references/evaluation-metrics-quickref.md)

---

## Objective Metrics

### 1. Completion Rate

**Definition**: Proportion of participants who successfully complete the task.

**Calculation**:
**Data source**: Manual observation + server logs (look for `step=success`)

**Reporting**: Percentage per task (e.g., "T1: 4/5 = 80%")

**Split by**:
- JS-on vs JS-off
- Keyboard-only vs mouse
- Screen reader vs visual

---

### 2. Time-on-Task

**Definition**: Duration from task start to completion or abandonment.

**Calculation**:
- **Server-timed**: `ms` column in metrics.csv (start to success event)
- **Backup**: Facilitator stopwatch (start when participant reads scenario, stop when they say "done")

**Reporting**:
- **Median** (primary): Middle value, resistant to outliers
- **MAD**: Median absolute deviation for spread
- **Range**: Min-max for context

**Example**:

**Split by**: JS-on vs JS-off (expect no-JS to be slower due to full page reloads)

---

### 3. Error Rate

**Definition**: Proportion of attempts that trigger validation errors.

**Calculation**:


**Data source**: `data/metrics.csv` where `step=validation_error`

**Reporting**: Percentage per task + qualitative notes on error type

**Example**:

**HCI insight**: High error rates → poor affordances, unclear constraints, or accessibility issues

---

### 4. Validation Error Count

**Definition**: Number of validation errors per participant per task.

**Calculation**: Count rows in `metrics.csv` with `step=validation_error` for given session + task

**Reporting**: Mean errors per task

**Example**:

---

## Subjective Metrics

### 5. Confidence Rating

**Definition**: Self-reported confidence that task was completed correctly.

**Scale**: 1 (not at all confident) → 5 (very confident)

**Collection method**: Ask immediately after each task:
> "On a scale of 1 to 5, how confident are you that you completed that task correctly?"

**Reporting**:
- Mean + standard deviation
- Distribution (how many rated 1, 2, 3, 4, 5)

**Example**:

**HCI insight**: Low confidence despite successful completion → interface doesn't provide sufficient feedback

---

### 6. Difficulty Rating (optional)

**Definition**: Perceived difficulty of task.

**Scale**: 1 (very easy) → 7 (very difficult)

**Collection method**: Post-task question:
> "How difficult was that task?"

**Reporting**: Mean ± SD per task

---

### 7. Post-Session Satisfaction (optional)

**Method**: 2-question UMUX-Lite (if time permits):
1. "This system's capabilities meet my requirements" (1–7: strongly disagree → strongly agree)
2. "This system is easy to use" (1–7: strongly disagree → strongly agree)

**Calculation**: Average of the two responses (higher = better perceived usability)

**Reporting**: Mean score across all participants

**Note**: UMUX-Lite takes <30 seconds, validated proxy for SUS (System Usability Scale)

---

## Qualitative Observations

### 8. Facilitator Notes

**Capture**:
- Hesitations ("Participant paused 10s before clicking filter")
- Verbalizations ("I'm not sure if it saved")
- Accessibility issues ("Screen reader didn't announce result count")
- Workarounds ("Used Ctrl+F instead of built-in filter")

**Format**: Timestamped notes in `pilot-notes.md`

**Analysis**: Thematic coding in Week 10 (group similar issues, link to backlog items)

---

## Accessibility-Specific Metrics

### 9. Keyboard-Only Completion

**Definition**: Can task be completed using only keyboard (no mouse)?

**Measurement**: Binary per task (yes/no)

**Reporting**: "T1: Keyboard-accessible ✅" or "T3: Tab order broken, failed ✗"

---

### 10. Screen Reader Announcement Quality

**Definition**: Are status messages and result counts announced appropriately?

**Measurement**: Qualitative note per task (announced / not announced / partial)

**Reporting**: List issues with WCAG references (4.1.3 Status Messages)

---

## Data Integrity Checks

Before analysis (Week 10):
- **Completeness**: All tasks have `session_id`, `task_code`, `step`
- **Plausibility**: Times within expected ranges (12s–120s for T1)
- **Consistency**: JS-mode matches observed condition
- **Outliers**: Flag times >3× median for review

Document any anomalies in `wk09/lab-wk9/research/data-notes.md`

---

## Summary Table

| Metric | Type | Source | Calculation | Reporting |
|--------|------|--------|-------------|-----------|
| Completion rate | Objective | Manual + logs | successes / attempts | % per task |
| Time-on-task | Objective | Server logs | Median + MAD | Seconds |
| Error rate | Objective | Server logs | errors / attempts | % per task |
| Confidence | Subjective | Post-task question | Mean ± SD | 1–5 scale |
| Facilitator notes | Qualitative | Manual observation | Thematic coding | Categories |
| KB-only completion | Accessibility | Manual test | Binary | ✅/✗ |
| SR announcements | Accessibility | SR observation | Qualitative | Issues list |
