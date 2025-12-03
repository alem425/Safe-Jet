# Contributing to FinalProject

Here is the workflow for adding new features to the project. We use **Pull Requests** to ensure all code is tested before it merges to `main`.

## Workflow Steps

### 1. Create a New Branch
**Never** push directly to `main`. Always create a feature branch first.

```bash
# Update your local main
git checkout main
git pull origin main

# Create your feature branch
git checkout -b feature/your-feature-name
```

### 2. Make Your Changes
Write your code, add resources, and make sure everything works locally.

### 3. Commit and Push
```bash
git add .
git commit -m "feat: add amazing new feature"
git push origin feature/your-feature-name
```

### 4. Open a Pull Request (PR)
1. Go to the [GitHub Repository](https://github.com/alem425/Final-Project)
2. You'll see a yellow banner saying "Compare & pull request". Click it!
3. Add a title and description of your changes.
4. Click **Create pull request**.

### 5. Wait for Checks
GitHub Actions will automatically run the build and tests.
- ✅ **Green Check**: All good! You can merge.
- ❌ **Red X**: Something failed. Click "Details" to see why, fix it in your branch, and push again.

### 6. Merge
Once the checks pass, click **"Merge pull request"** (or "Enable auto-merge" if you want it to merge automatically when ready).
