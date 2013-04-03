# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_SampleApp_session',
  :secret      => '62d72d87032f134586c413cd112a3fc5115588019692e60823040bc6236aa9743bec60ac44466fb0b23eacf666d9014c58d3811a6e9d5d50b6c057344b63534f'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
