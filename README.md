# Learning Resources for **GenAI for Busy Java Developers**

Welcome!

This repo is for students taking the O'Reilly course [GenAI for Busy Java Developers](https://learning.oreilly.com/live-events/genai-for-busy-java-developers/0642572177317).


## What You'll Learn
- Fundamentals of Machine Learning and Artificial Intelligence
- Connecting to LLMs 
- [LangChain4j](https://github.com/langchain4j/langchain4j): a Java-first library for LLM applications
- Prompt techniques
- The critical importance of context
- Chatbot architecture
- The value of embeddings and similarity
- Basic RAG architecture

## Tech Stack Needed
- Java 17+
- IDE - we will use [Intellij IDEA](https://www.jetbrains.com/idea/), but any IDE will work
- [LangChain4j](https://github.com/langchain4j/langchain4j)
- An API key from a GenAI provider
  * [OpenAI](https://platform.openai.com/)
  * [Google](https://ai.google.dev/gemini-api/docs/api-key)
  * [Anthropic](https://docs.anthropic.com/en/api/admin-api/apikeys/get-api-key)
  * [Mistral](https://docs.mistral.ai/api/)
- Access to the [code and resources repo](https://github.com/fgreco55/GenAIForBusyJavaDevelopers)

## Contributing

Got an idea or improvement?  
Open a [pull request](https://github.com/fgreco55/GenAIForBusyJavaDevelopers/pulls) or [file an issue](https://github.com/fgreco55/GenAIForBusyJavaDevelopers/issues).

- Fixes and improvements to examples or documentation
- New examples that showcase GenAI use cases in Java
- Questions, feedback, and suggestions

## Quick Start
```bash
   % git clone https://github.com/fgreco55/GenAIForBusyJavaDevelopers.git
   % cd GenAIForBusyJavaDevelopers
```

## Warning
Many of these examples utilize an external GenAI vendor such as OpenAI, Google, Anthropic, etc. When you use these services, you are sending your prompts and related context to that vendor. Be careful of what information you send to these vendors, as they may store your information on their servers and
potentially use your data to train their GenAI models. Many serious enterprises install GenAI LLMs on their machines (laptops, desktops, internal/private clouds) to avoid this 
serious concern.

I would suggest investigating [Ollama](https://ollama.com/), which is an open-source tool that simplifies installing local GenAI models.

